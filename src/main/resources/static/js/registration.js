async function testEmailExistence(email) {
    $.ajax({
        type: "GET",
        url: "/taken-emails/" + email,
        complete: function(response) {
            if (response.responseText == 'taken') {
                $('#email-taken-err-msg-container').html('' +
                    '<div class="alert alert-danger alert-dismissible fade show" role="alert">\n' +
                    '    <strong>Error: </strong>\n' +
                    '    <span>This e-mail address is already taken!</span>\n' +
                    '    <button type="button" class="close" data-dismiss="alert" aria-label="Close">\n' +
                    '        <span aria-hidden="true">&times;</span>\n' +
                    '    </button>\n' +
                    '</div>'
                );
            }
        }
    });
}