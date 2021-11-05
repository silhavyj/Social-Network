async function searchUsers(event) {
    event.preventDefault();
    let name = $('#searched_name').val();
    $.ajax({
        type: "GET",
        url: "/friends/search-all/" + name,
        complete: function(response) {
            $('#searched_users').html('');
            if (response.status != 200)
                return;
            let users = JSON.parse(response.responseText);
            let table = '';
            for (let index in users) {
                table +=
                    '<tr>' +
                    '   <td><img src="' + users[index]['profilePicturePath'] + '" class="img-thumbnail" style="width: 30%;"/></td>\n' +
                    '   <td class="align-middle"><h4><span class="badge badge-light">' + users[index]['firstname'] + ' ' + users[index]['lastname'] + '</span></h4></td>\n' +
                    '   <td class="align-middle"><h4><span class="badge badge-light">' + users[index]['email'] + '</span></h4></td>';

                if (users[index]['alreadyFriends'] === true) {
                    table += '<td class="align-middle"><p class="text-large">Request pending...</p></td>';
                } else {
                    table += '<td class="align-middle"><button class="btn btn-success">Send friend request</button>';
                }
                table += '</tr>';
            }
            $('#searched_users').html(table);
        }
    });
}