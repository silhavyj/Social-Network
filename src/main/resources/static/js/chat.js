let stompClient;
let sessionUserEmail;

function connectToChat() {
    sessionUserEmail = $('#session-user-email').text();
    var socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/online/' + sessionUserEmail, function (frame) {
            let data = JSON.parse(frame.body);
            if (data['onlineUserStatus'] == 'ONLINE') {
                createOnlineFriend(data);
            } else {
                deleteOnlineFriend(data);
            }
            console.log(data);
        });
    });
}

function createOnlineFriend(data) {
    const onlineUser = '' +
        '<li class="list-group-item" id="online-friend-' + data['email'] + '">\n' +
        '    <div>\n' +
        '     <i style="color: green;" class="fas fa-circle fa-xs"></i>\n' +
        '        <img src="' + data['profilePicturePath'] + ' " class="img-responsive rounded-circle" width="35" height="35"/>\n' +
        '        <span>' + data['fullName'] + '</span>\n' +
        '        <span class="circle">1</span>' +
        '    </div>\n' +
        '</li>';
    $("#online-friends").append(onlineUser);
}

function deleteOnlineFriend(data) {
    let onlineUser = document.getElementById('online-friend-' + data['email']);
    onlineUser.parentNode.removeChild(onlineUser);
}