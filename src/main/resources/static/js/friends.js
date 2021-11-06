async function searchUsers(event) {
    event.preventDefault();
    let name = $('#searched_name').val();
    $.ajax({
        type: "GET",
        url: "/friends/search-all/" + name,
        complete: function(response) {
            $('#friends-msg-container').html('');
            $('#searched_users').html('');
            if (response.status != 200) {
                $('#friends-msg-container').html(createErrorMessage(response.responseText));
                return;
            }
            let users = JSON.parse(response.responseText);
            let table = '';
            for (let index in users)
                table += createARecordInSearchResultsTable(users[index]);
            $('#searched_users').html(table);
        }
    });
}

function createErrorMessage(message) {
    return '<div class="alert alert-danger alert-dismissible fade show" role="alert">\n' +
        '   <strong>Error: </strong>\n' +
        '   <span>' + message + '</span>\n' +
        '   <button type="button" class="close" data-dismiss="alert" aria-label="Close">\n' +
        '   <span aria-hidden="true">&times;</span>\n' +
        '   </button>\n' +
        '</div>';
}

function createSuccessMessage(message) {
    return '<div class="alert alert-success alert-dismissible fade show" role="alert">\n' +
        '   <span>' + message + ' </span>\n' +
        '   <button type="button" class="close" data-dismiss="alert" aria-label="Close">\n' +
        '   <span aria-hidden="true">&times;</span>\n' +
        '   </button>\n' +
        '</div>';
}

function createARecordInSearchResultsTable(user) {
    let row = '<tr>' +
    '   <td><img src="' + user['profilePicturePath'] + '" class="img-thumbnail" style="width: 30%;"/></td>\n' +
    '   <td class="align-middle"><h5><span class="badge badge-light">' + user['firstname'] + ' ' + user['lastname'] + '</span></h5></td>\n' +
    '   <td class="align-middle"><h5><span class="badge badge-light">' + user['email'] + '</span></h5></td>';

    if (user['alreadyFriends'] === true) {
        row += '<td class="align-middle"><p class="text-large">Request pending...</p></td>';
    } else {
        row += '<td class="align-middle"><button class="btn btn-small btn-success" onclick="sendFriendRequest(this, ' + "'" + user['email'] + "'" + ')">Send friend request</button>';
    }
    row += '</tr>';
    return row;
}

function sendFriendRequest(tdObject, email) {
    $.ajax({
        type: "POST",
        url: "/friends/add-friend/" + email,
        complete: function(response) {
            $('#friends-msg-container').html('');
            if (response.status != 200) {
                $('#friends-msg-container').html(createErrorMessage(response.responseText));
                return;
            }
            $('#friends-msg-container').html(createSuccessMessage(response.responseText));

            let p = document.createElement('p');
            p.classList.add('text-large');
            p.innerHTML = 'Request pending...';

            let td = document.createElement('td');
            td.classList.add('align-middle');
            td.appendChild(p);

            tdObject.parentNode.appendChild(td);
            tdObject.parentNode.removeChild(tdObject);
        }
    });
}

function addPendingRequestRecordsInTable() {
    //pending-requests-table
}