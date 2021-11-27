// Creates a post
function submit_post() {
    const content = $('#new-post-content').val();
    if (content.trim().length == 0)
        return;
    $('#new-post-content').val('');

    let data = JSON.stringify(content);
    data = data.slice(1, data.length - 1);

    $.ajax({
        type: "POST",
        url: "/posts/normal-posts",
        contentType: "application/json",
        data: data,
        complete: function(response) {
            if (response.status == 200)
                fetchLatestPosts();
        }
    });
}

// Fetches down all the user's latest posts
function fetchUsersPosts() {
    $.ajax({
        type: "GET",
        url: "/posts/normal-posts",
        complete: function(response) {
            if (response.status != 200)
                return;
            let posts = JSON.parse(response.responseText);
            let html = '';
            let modals = '';

            // Render the post
            for (let index in posts) {
                html += createPost(posts[index], posts[index]['user']);
                modals += createPopUpOfPeopleWhoLikedPost(posts[index]);
            }
            $('#posts').html(html);
            $('#modals').html(modals);
        }
    });
}

// Sets up a times that updates the posts on the main page every 60s
function setFetchPostsTimer() {
    fetchLatestPosts();
    setInterval(function(){
        fetchLatestPosts();
    }, 60 * 1000);
}

// Fetches down all the latest posts (posts + announcements)
function fetchLatestPosts() {
    $.ajax({
        type: "GET",
        url: "/posts",
        complete: function(response) {
            if (response.status != 200)
                return;
            console.log(response);
            let posts = JSON.parse(response.responseText)['posts'];
            let user = JSON.parse(response.responseText)['user'];
            let html = '';
            let modals = '';

            // Render the posts
            for (let index in posts) {
                html += createPost(posts[index], user);
                modals += createPopUpOfPeopleWhoLikedPost(posts[index]);
            }
            $('#posts').html(html);
            $('#modals').html(modals);
        }
    });
}

// Creates a pop up modal of people who have liked the post/announcement
function createPopUpOfPeopleWhoLikedPost(post) {
    return '    <div class="modal fade" id="' + post['id'] + 'Modal" tabindex="-1" role="dialog" aria-labelledby="' + post['id'] + 'Modal" aria-hidden="true">\n' +
        '        <div class="modal-dialog" role="document">\n' +
        '            <div class="modal-content">\n' +
        '                <div class="modal-body">\n' +
        '                    <ul class="list-group">' +  generateListOfPeopleWhoLikesPost(post) + '</ul>\n' +
        '                </div>\n' +
        '                <div class="modal-footer">\n' +
        '                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '    </div>';
}

// Generates a list (<ul>) of people who have liked the post/announcement
function generateListOfPeopleWhoLikesPost(post) {
    let html = '';
    for (let index in post['likes'])
        html += generatePersonWhoLikedPost(post['likes'][index]);
    return html;
}

// Generates an <li> containing a person who has like the post/announcement
function generatePersonWhoLikedPost(like) {
    return '<li class="list-group-item"><img class="img-responsive rounded-circle" width="30" height="30" src="' + like['user']['profilePicturePath'] +'"/><span> ' + like['user']['firstname'] + ' ' +  like['user']['lastname'] + '</span></li>';
}

// Checks if the posts is created by the session user
function isUsersPosts(post, user) {
    return post['user']['email'] == user['email'];
}

// Checks if the user has already liked the post/announcement
function hasUserAlreadyLiked(likes, user) {
    for (let i in likes) {
        if (likes[i]['user']['email'] == user['email'])
            return true;
    }
    return false;
}

// Generates the action of the like button (like/unlike)
function generateLikeOnClickEven(post, user) {
    if (hasUserAlreadyLiked(post['likes'], user))
        return 'onclick="unlikePost(' + post['id'] + ')"';
    return 'onclick="likePost(' + post['id'] + ')"';
}

// Renders a post/announcement
function createPost(post, user) {
    const usersPost = isUsersPosts(post, user);
    const hasBeenLiked = hasUserAlreadyLiked(post['likes'], user);

    return '    <div class="card mt-4 text-dark ' + (post['postType'] == "ANNOUNCEMENT" ? "border-primary " : " ") + 'mb-3" style="width: 70%; margin: auto">\n' +
        '      <div class="card-header">\n' +
        '        <div class="container">\n' +
        '          <div class="row">\n' +
        '            <div class="col-3">\n' +
        '              <img src="' + post['user']['profilePicturePath'] + '" class="img-responsive rounded-circle" width="70" height="70">\n' +
        '            </div>\n' +
        '            <div class="col-9">\n' +
        '              <div class="row">\n' +
        '                <span>' + post['postedAt'] + '</span>\n' +
        '              </div>\n' +
        '              <div class="row">\n' +
        '                <span><strong>' + post['user']['firstname'] + ' ' +  post['user']['lastname'] + '</strong></span>\n' +
        '              </div>\n' +
        '            </div>\n' +
        '          </div>\n' +
        '        </div>\n' +
        '      </div>\n' +
        '      <div class="card-body ' + (post['postType'] == "ANNOUNCEMENT" ? "text-primary" : "") + '">\n' +
        '        <p class="card-text">' + post['content'] + '</p>\n' +
        '      </div>\n' +
        '      <div class="card-footer text-muted">\n' +
        '        <div class="row">\n' +
        '          <div class="col-3">\n' +
        '             <span><i ' + (!usersPost ? generateLikeOnClickEven(post, user) : '') + ' ' + (!usersPost ? 'style="cursor:pointer;"' : '') + ' class="' + (hasBeenLiked && !usersPost ? 'text-dark' : '') + ' fas fa-thumbs-up"></i> ' + post['likes'].length + '</span>\n' +
        '          </div>\n' +
        '          <div class="col-9">\n' +
        '             <span style="cursor: pointer;" data-toggle="modal" data-target="#' + post['id'] + 'Modal"><i class="fa fa-list" aria-hidden="true"></i> Show list of people</span>\n' +
        '          </div>\n' +
        '         </div>'+
        '      </div>\n' +
        '    </div>';
}

// Likes a post
function likePost(postId) {
    $.ajax({
        type: "POST",
        url: "/posts/" + postId + "/likes",
        complete: function(response) {
            location.reload();
        }
    });
}

// Unlikes a post
function unlikePost(postId) {
    $.ajax({
        type: "DELETE",
        url: "/posts/" + postId + "/likes",
        complete: function(response) {
            location.reload();
        }
    });
}

// Fetches down all the user's latest announcements
function fetchAnnouncements() {
    $.ajax({
        type: "GET",
        url: "/posts/announcements",
        complete: function(response) {
            if (response.status != 200)
                return;
            let posts = JSON.parse(response.responseText);
            let html = '';
            let modals = '';

            for (let index in posts) {
                html += createPost(posts[index], posts[index]['user']);
                modals += createPopUpOfPeopleWhoLikedPost(posts[index]);
            }
            $('#announcements').html(html);
            $('#modals').html(modals);
        }
    });
}

// Creates an announcement
function submit_announcement() {
    const content = $('#new-announcement-content').val();
    if (content.trim().length == 0)
        return;

    $('#new-post-content').val('');
    let data = JSON.stringify(content);
    data = data.slice(1, data.length - 1);

    $.ajax({
        type: "POST",
        url: "/posts/announcements",
        contentType: "application/json",
        data: data,
        complete: function(response) {
            location.reload();
        }
    });
}