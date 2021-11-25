let stompClient;
let sessionUserEmail;

const profileImages = new Map();
let selectedFriend = null;
const messages = new Map();

function connectToChat() {
    sessionUserEmail = $('#session-user-email').text();
    var socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/messages/' + sessionUserEmail, function (frame) {
            let data = JSON.parse(frame.body);
            if (data['messageType'] == 'USER_ONLINE') {
                createOnlineFriend(data);
            } else if (data['messageType'] == 'USER_OFFLINE') {
                deleteOnlineFriend(data);
            } else if (data['messageType'] == 'MESSAGE') {
                receivedMessage(data);
            }
        });
    });
}

function createOnlineFriend(data) {
    profileImages.set(data['senderEmail'], [data['profilePicturePath'], data['senderFullName']]);
    const onlineUser = '' +
        '<li onclick=\'openChatWindow("' + data['senderEmail'] + '")\' class="list-group-item" id="online-friend-' + data['senderEmail'] + '" style="cursor:pointer;">\n' +
        '    <div>\n' +
        '     <i style="color: green;" class="fas fa-circle fa-xs"></i>\n' +
        '        <img src="' + data['profilePicturePath'] + ' " class="img-responsive rounded-circle" width="35" height="35"/>\n' +
        '        <span>' + data['senderFullName'] + '</span>\n' +
        '        <span id="unread-messages-' + data['senderEmail'] + '" class="circle" style="display: none;">0</span>' +
        '    </div>\n' +
        '</li>';
    $("#online-friends").append(onlineUser);
}

function deleteOnlineFriend(data) {
    profileImages.delete(data['senderEmail']);
    let onlineUser = document.getElementById('online-friend-' + data['senderEmail']);
    onlineUser.parentNode.removeChild(onlineUser);
}
function closeForm() {
    selectedFriend = null;
    $("#chat-window").css("display", "none");
}

function openChatWindow(userEmail) {
    if (selectedFriend != null && selectedFriend == userEmail) {
        closeForm();
        return;
    }
    selectedFriend = userEmail;
    $("#chat-window").css("display", "block");

    const unreadMessagesId = 'unread-messages-' + userEmail;
    document.getElementById(unreadMessagesId).style.display = "none";
    document.getElementById(unreadMessagesId).innerHTML = "0";

    $("#chat-window-profile-pic").attr('src', profileImages.get(userEmail)[0]);
    $("#chat-window-username").html( profileImages.get(userEmail)[1]);

    const messagesContainer = document.getElementById('messages-container');
    messagesContainer.innerHTML = '';
    const conversation = messages.get(userEmail);
    for (let index in conversation) {
        messagesContainer.innerHTML += renderMessage(conversation[index]);
    }
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function renderMessage(message) {
    message = JSON.parse(message);
    if (message['messageType'] === 'MESSAGE') {
        const parts = message['timeStamp'].split('T');
        const part1 = parts[0].split('-');
        const part2 = parts[1].split('.')[0];
        const timeStamp = part1[0] + '/' + part1[1] + '/' + part1[2] + ' ' + part2;

        return ' ' +
            '<div class="row">\n' +
            '    <div class="container">\n' +
            '        <div class="row">\n' +
            '            <img src="' + message['profilePicturePath'] + '" class="img-responsive rounded-circle mr-3" width="30" height="30">\n' +
            '             <p class="p-2 bg-dark text-white outcoming-message">' + message['message'] + '</p>\n' +
            '        </div>\n' +
            '        <div class="row">\n' +
            '            <p class="text-secondary message-timestamp">' + timeStamp + '</p>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</div>';
    } else if (message['messageType'] === 'SENT') {
        return '' +
            '<div class="row">\n' +
            '    <div class="container">\n' +
            '         <p class="p-2 bg-light text-dark incoming-message">' + message['message'] + '</p>\n' +
            '    </div>\n' +
            '    <div class="container">\n' +
            '         <p class="text-secondary message-timestamp message-timestamp-right">' + message['timeStamp'] + '</p>\n' +
            '    </div>\n' +
            '</div>';
    }
    return '';
}

function getDateTime() {
    let now     = new Date();
    let year    = now.getFullYear();
    let month   = now.getMonth()+1;
    let day     = now.getDate();
    let hour    = now.getHours();
    let minute  = now.getMinutes();
    let second  = now.getSeconds();
    if (month.toString().length == 1) {
        month = '0'+month;
    }
    if (day.toString().length == 1) {
        day = '0'+day;
    }
    if (hour.toString().length == 1) {
        hour = '0'+hour;
    }
    if (minute.toString().length == 1) {
        minute = '0'+minute;
    }
    if (second.toString().length == 1) {
        second = '0'+second;
    }
    const dateTime = year+'/'+month+'/'+day+' '+hour+':'+minute+':'+second;
    return dateTime;
}

function sendMessage() {
    const message = $('#message-input').val();
    if (message.trim().length == 0)
        return;
    $('#message-input').val("");

    let conversation = messages.get(selectedFriend);
    if (conversation == null)
        conversation = []

    let timeStamp = getDateTime();
    conversation.push(JSON.stringify({
        messageType: 'SENT',
        message: message,
        timeStamp: timeStamp
    }));
    messages.set(selectedFriend, conversation);

    stompClient.send("/app/chat/" + selectedFriend, {}, JSON.stringify({
        senderFullName: '',
        senderEmail: '',
        profilePicturePath: '',
        messageType: 'MESSAGE',
        message: message
    }));
    const messagesContainer = document.getElementById('messages-container');
    messagesContainer.innerHTML += renderMessage(conversation[conversation.length - 1]);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function receivedMessage(data) {
    if (selectedFriend != data['senderEmail']) {
        const unreadMessagesId = 'unread-messages-' + data['senderEmail'];
        const unreadMessagesSpan = document.getElementById(unreadMessagesId);
        let unreadMessages = parseInt(unreadMessagesSpan.innerHTML);
        unreadMessages++;
        unreadMessagesSpan.style.display = "inline-block";
        unreadMessagesSpan.innerHTML = unreadMessages;
    }
    let conversation = messages.get(data['senderEmail']);
    if (conversation == null)
        conversation = []
    conversation.push(JSON.stringify(data));
    messages.set(data['senderEmail'], conversation);

    if (selectedFriend == data['senderEmail']) {
        const messagesContainer = document.getElementById('messages-container');
        messagesContainer.innerHTML += renderMessage(conversation[conversation.length - 1]);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
}