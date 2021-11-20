function submit_post() {
    const content = $('#new-post-content').val();
    if (content === "")
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

function fetchUsersPosts() {
    $.ajax({
        type: "GET",
        url: "/posts/normal-posts",
        complete: function(response) {
            if (response.status != 200)
                return;
            let posts = JSON.parse(response.responseText);
            let html = '';
            for (let index in posts)
                html += createPost(posts[index], posts[index]['user']);
            $('#posts').html(html);
        }
    });
}

function setFetchPostsTimer() {
    fetchLatestPosts();
    setInterval(function(){
        fetchLatestPosts();
    }, 60 * 1000);
}

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
            for (let index in posts)
                html += createPost(posts[index], user);
            $('#posts').html(html);
        }
    });
}

function isUsersPosts(post, user) {
    return post['user']['email'] == user['email'];
}

function hasUserAlreadyLiked(likes, user) {
    for (let i in likes) {
        if (likes[i]['user']['email'] == user['email'])
            return true;
    }
    return false;
}

function generateLikeOnClickEven(post, user) {
    if (hasUserAlreadyLiked(post['likes'], user))
        return 'onclick="unlikePost(' + post['id'] + ')"';
    return 'onclick="likePost(' + post['id'] + ')"';
}

function createPost(post, user) {
    const usersPost = isUsersPosts(post, user);
    const hasBeenLiked = hasUserAlreadyLiked(post['likes'], user);

    return '    <div class="card mt-4 text-dark ' + (post['postType'] == "ANNOUNCEMENT" ? "border-primary " : " ") + 'mb-3" style="width: 70%; margin: auto">\n' +
        '      <div class="card-header">\n' +
        '        <div class="container">\n' +
        '          <div class="row">\n' +
        '            <div class="col-3">\n' +
        '              <img src="' + post['user']['profilePicturePath'] + '" class="img-responsive" width="70" height="70">\n' +
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
        '         </div>'+
        '      </div>\n' +
        '    </div>';
}

function likePost(postId) {
    $.ajax({
        type: "POST",
        url: "/posts/" + postId + "/likes",
        complete: function(response) {
            location.reload();
        }
    });
}

function unlikePost(postId) {
    $.ajax({
        type: "DELETE",
        url: "/posts/" + postId + "/likes",
        complete: function(response) {
            location.reload();
        }
    });
}

function fetchAnnouncements() {
    $.ajax({
        type: "GET",
        url: "/posts/announcements",
        complete: function(response) {
            if (response.status != 200)
                return;
            let posts = JSON.parse(response.responseText);
            let html = '';
            for (let index in posts)
                html += createPost(posts[index], posts[index]['user']);
            $('#announcements').html(html);
        }
    });
}

function submit_announcement() {
    const content = $('#new-announcement-content').val();
    if (content === "")
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