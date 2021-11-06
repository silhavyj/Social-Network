function entropy(password, inputElementId) {
    let password_strength = document.getElementById(inputElementId);
    if (password.length == 0) {
        password_strength.innerHTML = "";
        return;
    }
    let regex = new Array();
    regex.push("[A-Z]");
    regex.push("[a-z]");
    regex.push("[0-9]");
    regex.push("[ !”#$%&’()*+,-./:;<=>?@[\\]^_`{|}~]");

    const poolSizes = [26, 26, 10, 33];
    let poolSize = 0;

    for (let i = 0; i < regex.length; i++) {
        if (new RegExp(regex[i]).test(password)) {
            poolSize += poolSizes[i];
        }
    }
    let strength = '';
    const entropy = Math.floor(Math.log2(Math.pow(poolSize, password.length)))

    if (entropy < 28) {
        strength = "<small class='progress-bar bg-danger' style='width: 20%'>Very Weak (" + entropy + ")</small>";
    } else if (entropy >= 28 && entropy <= 35) {
        strength = "<small class='progress-bar bg-warning' style='width: 40%'>Weak (" + entropy + ")</small>";
    } else if (entropy >= 36 && entropy <= 59) {
        strength = "<small class='progress-bar bg-info' style='width: 60%'>Reasonable (" + entropy + ")</small>";
    } else if (entropy >= 60 && entropy <= 127) {
        strength = "<small class='progress-bar bg-success' style='width: 80%'>Strong (" + entropy + ")</small>";
    } else {
        strength = "<small class='progress-bar bg-success' style='width: 100%'>Very Strong " + entropy + "</small>";
    }
    password_strength.innerHTML = strength;
}