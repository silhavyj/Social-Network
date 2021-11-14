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
                html += createPost(posts[index]);
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
            let posts = JSON.parse(response.responseText);
            let html = '';
            for (let index in posts)
                html += createPost(posts[index]);
            $('#posts').html(html);
        }
    });
}

function createPost(post) {
    return '    <div class="card mt-4 text-dark ' + (post['postType'] == "ANNOUNCEMENT" ? "bg-info " : " ") + 'mb-3" style="width: 70%; margin: auto">\n' +
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
        '      <div class="card-body">\n' +
        '        <p class="card-text">' + post['content'] + '</p>\n' +
        '      </div>\n' +
        '      <div class="card-footer text-muted">\n' +
        '      </div>\n' +
        '    </div>';
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
                html += createPost(posts[index]);
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