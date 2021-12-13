// searches users in the database
async function searchUsers(event) {
    // disable default actions
    event.preventDefault();

    // make sure the searched name isn't empty
    let name = $('#searched_name').val();
    if (name == "")
        name = "1";

    $.ajax({
        type: "GET",
        url: "/people/" + name,
        complete: function(response) {
            $('#friends-msg-container').html('');
            $('#searched_users').html('');
            if (response.status != 200) {
                console.log(response);
                $('#friends-msg-container').html(createErrorMessage(response.responseText));
                return;
            }
            // parse the response
            let users = JSON.parse(response.responseText);
            let table = '';

            // display an info message saying that no users were found
            if (users.length == 0) {
                $('#friends-msg-container').html(createInfoMessage('No users were found'));
                return;
            }

            // display found users
            for (let index in users)
                table += createARecordInSearchResultsTable(users[index]);
            $('#searched_users').html(table);
        }
    });
}

// Creates an error message
function createErrorMessage(message) {
    return '<div class="alert alert-danger alert-dismissible fade show" role="alert">\n' +
        '   <strong>Error: </strong>\n' +
        '   <span>' + message + '</span>\n' +
        '   <button type="button" class="close" data-dismiss="alert" aria-label="Close">\n' +
        '   <span aria-hidden="true">&times;</span>\n' +
        '   </button>\n' +
        '</div>';
}

// Creates an info message
function createInfoMessage(message) {
    return '<div class="alert alert-info alert-dismissible fade show" role="alert">\n' +
        '   <span>' + message + '</span>\n' +
        '   <button type="button" class="close" data-dismiss="alert" aria-label="Close">\n' +
        '   <span aria-hidden="true">&times;</span>\n' +
        '   </button>\n' +
        '</div>';
}

// Creates one found user (result of the search)
function createARecordInSearchResultsTable(user) {
    let row = '<tr>' +
    '   <td><img src="' + user['profilePicturePath'] + '" class="img-thumbnail" style="width: 30%;"/></td>\n' +
    '   <td class="align-middle"><h5><span class="badge badge-light">' + user['firstname'] + ' ' + user['lastname'] + '</span></h5></td>\n' +
    '   <td class="align-middle"><h5><span class="badge badge-light">' + user['email'] + '</span></h5></td>';

    if (user['status'] === "PENDING") {
        row += '<td class="align-middle"><p class="text-large">Request pending...</p></td>';
    } else {
        row += '<td class="align-middle"><button class="btn btn-small btn-success" onclick="sendFriendRequest(this,' + "'" + user['email'] + "'" + ')">Send friend request</button>';
    }
    row += '</tr>';
    return row;
}

// Sends a friend request
function sendFriendRequest(tdObject, email) {
    $.ajax({
        type: "POST",
        url: "/friends/" + email,
        complete: function(response) {
            $('#friends-msg-container').html('');
            if (response.status != 200) {
                $('#friends-msg-container').html(createErrorMessage(response.responseText));
                return;
            }
            location.reload()
        }
    });
}

// Cancels a friend request
async function cancelFriendRequest(email) {
    $.ajax({
        type: "DELETE",
        url: "/friends/" + email,
        complete: function() {
            location.reload()
        }
    });
}

// Accepts a friend request
async function acceptFriendRequest(email) {
    $.ajax({
        type: "PUT",
        url: "/friends/" + email,
        complete: function() {
            location.reload()
        }
    });
}

// BLocks a user
async function blockUser(email) {
    $.ajax({
        type: "PUT",
        url: "/friends/blocked/" + email,
        complete: function() {
            location.reload()
        }
    });
}

// Unblocks a user
async function unblockUser(email) {
    $.ajax({
        type: "DELETE",
        url: "/friends/blocked/" + email,
        complete: function() {
            location.reload()
        }
    });
}

// make user an admin
async function escalateToAdmin(email) {
    $.ajax({
        type: "PUT",
        url: "/admin/" + email,
        complete: function() {
            location.reload()
        }
    });
}

// remove admin privileges from a user
async function removeAdminPrivileges(email) {
    $.ajax({
        type: "DELETE",
        url: "/admin/" + email,
        complete: function() {
            location.reload()
        }
    });
}
