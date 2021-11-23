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
    console.log('BBBB ' + conversation);
    for (let index in conversation) {
        console.log('AAAA ' + conversation[index]);
        messagesContainer.innerHTML += renderMessage(conversation[index]);
    }
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

function renderMessage(message) {
    if (message['messageType'] === 'MESSAGE') {
        return ' ' +
            '<div class="row">\n' +
            '    <div class="container">\n' +
            '        <div class="row">\n' +
            '            <img src="' + message['profilePicturePath'] + '" class="img-responsive rounded-circle mr-3" width="30" height="30">\n' +
            '             <p class="p-2 bg-dark text-white outcoming-message">' + message['message'] + '</p>\n' +
            '        </div>\n' +
            '        <div class="row">\n' +
            '            <p class="text-secondary message-timestamp">15. 9. 2015</p>\n' +
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
            '         <p class="text-secondary message-timestamp message-timestamp-right">15. 9. 2015</p>\n' +
            '    </div>\n' +
            '</div>';
    }
    return '';
}

function sendMessage() {
    const message = $('#message-input').val();
    if (message === "")
        return;
    $('#message-input').val("");

    let conversation = messages.get(selectedFriend);
    if (conversation == null)
        conversation = []
    conversation.push({
        messageType: 'SENT',
        message: message
    });
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
        unreadMessagesSpan.style.display = "block";
        unreadMessagesSpan.innerHTML = unreadMessages;
    }
    let conversation = messages.get(selectedFriend);
    if (conversation == null)
        conversation = []

    conversation.push(data);
    messages.set(selectedFriend, conversation);
    const messagesContainer = document.getElementById('messages-container');
    messagesContainer.innerHTML += renderMessage(conversation[conversation.length - 1]);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}