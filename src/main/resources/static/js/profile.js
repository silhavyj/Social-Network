function fetchUsersPosts() {
    $.ajax({
        type: "GET",
        url: "/posts/normal-posts",
        complete: function(response) {
            let posts = JSON.parse(response.responseText);
            let html = '';
            for (let index in posts)
                html += createPost(posts[index]);
            $('#posts').html(html);
        }
    });
}

function createPost(post) {
    return '    <div class="card mt-4 text-dark mb-3" style="width: 70%; margin: auto">\n' +
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