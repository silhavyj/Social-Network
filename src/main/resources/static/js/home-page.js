function submit_post() {
    const content = $('#new-post-content').val();
    if (content == "")
        return;

    let data = JSON.stringify(content);
    data = data.slice(1, data.length - 1);

    $.ajax({
        type: "POST",
        url: "/posts/normal-posts",
        contentType: "application/json",
        data: data,
        complete: function(response) {
            // TODO fetch latest posts
        }
    });
}