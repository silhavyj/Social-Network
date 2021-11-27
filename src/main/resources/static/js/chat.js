// IP address & port of where the application is running
const URL = "http://127.0.0.1:8085";

// stomp client (web sockets)
let stompClient;

// e-mail address of the session user (the client)
let sessionUserEmail;

// map of profile images (key = e-mail address; value = profile image path)
const profileImages = new Map();

// selected friend (opened chat window)
let selectedFriend = null;

// map of messages with all friends
// (key = e-mail address - friend; value = list of all messages)
const messages = new Map();

// Connects to the web socket end-point and starts
// subscribing to it.
function connectToChat() {
    sessionUserEmail = $('#session-user-email').text();
    var socket = new SockJS(URL + "/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/messages/' + sessionUserEmail, function (frame) {
            // parse the data received from the server
            let data = JSON.parse(frame.body);

            if (data['messageType'] == 'USER_ONLINE') {
                // if it's a notification of an online friend
                createOnlineFriend(data);
            } else if (data['messageType'] == 'USER_OFFLINE') {
                // if it's a notification of an offline friend
                deleteOnlineFriend(data);
            } else if (data['messageType'] == 'MESSAGE') {
                // if it's a message received from a friend
                receivedMessage(data);
            }
        });
    });
}

// Creates an online user (box with their name)
function createOnlineFriend(data) {
    profileImages.set(data['senderEmail'], [data['profilePicturePath'], data['senderFullName']]);
    const onlineUser = '' +
        '<li onclick=\'openChatWindow("' + data['senderEmail'] + '")\' class="list-group-item" id="online-friend-' + data['senderEmail'] + '" style="cursor:pointer;">\n' +
        '    <div>\n' +
        '       <i style="color: green;" class="fas fa-circle fa-xs"></i>\n' +
        '        <img src="' + data['profilePicturePath'] + ' " class="img-responsive rounded-circle" width="35" height="35"/>\n' +
        '        <span>' + data['senderFullName'] + '</span>\n' +
        '        <span id="unread-messages-' + data['senderEmail'] + '" class="circle" style="display: none;">0</span>' +
        '    </div>\n' +
        '</li>';
    $("#online-friends").append(onlineUser);
}

// Deletes an online user (they just went offline)
function deleteOnlineFriend(data) {
    profileImages.delete(data['senderEmail']);
    let onlineUser = document.getElementById('online-friend-' + data['senderEmail']);
    onlineUser.parentNode.removeChild(onlineUser);
}

// Closes up the chat window
function closeForm() {
    selectedFriend = null;
    $("#chat-window").css("display", "none");
}

// Opens up the chat window
function openChatWindow(userEmail) {
    if (selectedFriend != null && selectedFriend == userEmail) {
        closeForm();
        return;
    }
    // stores the selected friend
    selectedFriend = userEmail;

    // display the chat window
    $("#chat-window").css("display", "block");

    // "set all messages as read" - the read bubble
    const unreadMessagesId = 'unread-messages-' + userEmail;
    document.getElementById(unreadMessagesId).style.display = "none";
    document.getElementById(unreadMessagesId).innerHTML = "0";

    // sets the header of the chat window - profile picture & name
    $("#chat-window-profile-pic").attr('src', profileImages.get(userEmail)[0]);
    $("#chat-window-username").html( profileImages.get(userEmail)[1]);

    // Clears out the previous conversation
    const messagesContainer = document.getElementById('messages-container');
    messagesContainer.innerHTML = '';

    // Updates the chat window with the conversation with the selected user
    const conversation = messages.get(userEmail);
    for (let index in conversation) {
        messagesContainer.innerHTML += renderMessage(conversation[index]);
    }
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// Creates a chat message
function renderMessage(message) {
    message = JSON.parse(message);

    // Check if it's a received or sent message
    if (message['messageType'] === 'MESSAGE') {

        // format the datetime
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

// Returns the current datetime
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

// Sends a message to a friend
function sendMessage() {
    // message content
    const message = $('#message-input').val();

    // make sure it's not an empty message
    if (message.trim().length == 0)
        return;
    $('#message-input').val("");

    // Adds the message into the proper conversation (data structure)
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

    // Sends the message off to the the server
    stompClient.send("/app/chat/" + selectedFriend, {}, JSON.stringify({
        senderFullName: '',
        senderEmail: '',
        profilePicturePath: '',
        messageType: 'MESSAGE',
        message: message
    }));

    // Render the message and add it to the chat window
    const messagesContainer = document.getElementById('messages-container');
    messagesContainer.innerHTML += renderMessage(conversation[conversation.length - 1]);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// Receives a message from the server
function receivedMessage(data) {
    // Add the message into the proper conversation (data structure)
    let conversation = messages.get(data['senderEmail']);
    if (conversation == null)
        conversation = []
    conversation.push(JSON.stringify(data));
    messages.set(data['senderEmail'], conversation);

    // Update the unread messages counter if the message didn't come from the
    // currently selected user.
    if (selectedFriend != data['senderEmail']) {
        const unreadMessagesId = 'unread-messages-' + data['senderEmail'];
        const unreadMessagesSpan = document.getElementById(unreadMessagesId);
        let unreadMessages = parseInt(unreadMessagesSpan.innerHTML);
        unreadMessages++;
        unreadMessagesSpan.style.display = "inline-block";
        unreadMessagesSpan.innerHTML = unreadMessages;
    } else {
        // If the message came from the currently selected user, render the message
        // and add it into the chat window
        const messagesContainer = document.getElementById('messages-container');
        messagesContainer.innerHTML += renderMessage(conversation[conversation.length - 1]);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
}