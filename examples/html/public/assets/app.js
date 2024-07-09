const ui = {
    form        : document.getElementById('form'),
    message     : document.getElementById('message'),
    messages    : document.getElementById('messages'),
    init        : () =>{
        ui.form.addEventListener('submit', event => {
            event.preventDefault();
            ui.sendMessage();
        });
    },
    sendMessage : () => {
        let value = ui.message.value.trim();
        if ( value !== '' ){
            ws.publish( 'chat', value );
            ui.message.value = '';
        }
    },
    writeLine   : ( message, type ) => {
        const li = document.createElement('li');
        li.classList.add('py-2','px-3','rounded-4');
        if ( type === 'sent' )
            li.classList.add('text-end','bg-primary','text-white');
        else
            li.classList.add('bg-secondary-subtle','text-body-emphasis');
        li.textContent = message;
        ui.messages.appendChild(li);
        ui.messages.scrollTo({
            top: ui.messages.scrollHeight,
            behavior: 'smooth'
        });
    },
};

const ws = {
    id        : null,
    isOpen    : false,
    namespace : '',
    init      : function( uri ){
        this.wsConnection           = new WebSocket( uri );
        this.wsConnection.onopen    = this.onOpen.bind( this );
        this.wsConnection.onclose   = this.onClose.bind( this );
        this.wsConnection.onerror   = this.onError.bind( this );
        this.wsConnection.onmessage = this.onMessage.bind( this );
        return this;
    },
    onMessage : function( event ){
        try {
            let data = JSON.parse( event.data );

            switch( data.type ){
                case 'welcome':
                    this.id = data.sessionId;
                    break;
                case 'data':
                    ui.writeLine(
                        data.data,
                        data.publisherId === this.id ? 'sent' : 'received'
                    );
                    break;

            }
        }
        catch ( error ) {
            console.error('Error parsing JSON:', error);
        }
    },
    onOpen : function(){
        this.isOpen = this.isConnected();
        if ( this.isOpen )
            this.subscribe( 'chat' );
    },
    onError : function( error ){
        console.error('WebSocket error:', error);
    },
    onClose : function(){
        this.isOpen = false;
        console.log('WebSocket connection closed');
    },
    subscribe : function( channel ){
        this.wsConnection.send( JSON.stringify({
            type      : 'subscribe',
            channel   : channel,
            namespace : this.namespace,
        }));
        return true;
    },
    unsubscribe : function( channel ){
        this.wsConnection.send( JSON.stringify({
            type      : 'unsubscribe',
            channel   : channel,
            namespace : this.namespace,
        }));
        return true;
    },
    publish : function( channel, content ){
        this.wsConnection.send( JSON.stringify({
            type      : 'publish',
            channel   : channel,
            content   : content,
            namespace : this.namespace,
        }));
        return true;
    },
    getSubscriptions : function(){
        this.wsConnection.send( JSON.stringify({
            type      : 'getSubscriptions',
            namespace : this.namespace,
        }));
        return true;
    },
    getSubscriberCount : function( channel ){
        this.wsConnection.send( JSON.stringify({
            type      : 'getSubscriberCount',
            channel   : channel,
            namespace : this.namespace,
        }));
        return true;
    },
    isConnected : function(){
        return this.wsConnection !== null && this.wsConnection.readyState === WebSocket.OPEN;
    },
};

ws.init( 'ws://192.168.10.50:8081/ws/' );
ui.init();
