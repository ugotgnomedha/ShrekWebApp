var filter = $("#filter-section");
var opened = true;
$("#filterButton").click(function () {
    if (!opened) {
        filter.removeClass("filter-section-closed");
        opened = true;
    } else {
        filter.addClass("filter-section-closed");
        opened = false;
    }
});


const btn = document.querySelector("#rightButton");

function sendData(data) {
    console.log('Sending data');

    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;

    // Turn the data object into an array of URL-encoded key/value pairs.
    for (name in data) {
        urlEncodedDataPairs.push(encodeURIComponent(name) + '=' + encodeURIComponent(data[name]));
    }

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');

    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    // Set up our request
    XHR.open('POST', '/moveView');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
}


$("#play-button").click(function () {
    $('#preSetForm').submit();
});

// $("#add-domen").click(function () {
//     $('#domenForm').submit();
// });

$("#add-preSet").click(function () {
    let data = document.getElementById("searchTxt").value;
    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;

    urlEncodedDataPairs.push(encodeURIComponent('name') + '=' + encodeURIComponent(data));
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');

    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    XHR.open('POST', '/addPreSet');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    // alert(data);
    $("#add-form").submit();

});

$("#add-new-domen").click(function () {
    let data = document.getElementById("searchTxt").value;
    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;

    urlEncodedDataPairs.push(encodeURIComponent('name') + '=' + encodeURIComponent(data));
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');

    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    XHR.open('POST', '/addDomen');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    // alert(data);
    $("#add-form").submit();

});

// Pass the checkbox name to the function
function getCheckedBoxes(checkboxName) {
    var checkboxes = document.getElementById(checkboxName);
    var checkboxesChecked = [];
    // loop over them all
    for (var i = 0; i < checkboxes.length; i++) {
        // And stick the checked ones onto an array...
        if (checkboxes[i].checked) {
            checkboxesChecked.push(checkboxes[i]);
        }
    }
    // Return the array if it is non-empty, or null
    return checkboxesChecked.length > 0 ? checkboxesChecked : null;
}

$("#live-edit-button-add").click(function () {
    flag = false;
    var checkBoxesPreSets = getCheckedBoxes("preSetForm");
    var checkPreSets = "";
    for (var i = 0; i < checkBoxesPreSets.length; i++) {
        if (flag) {
            checkPreSets = checkPreSets + "!" + (checkBoxesPreSets[i].value);
        } else {
            checkPreSets = checkBoxesPreSets[i].value;
            flag = !flag;
        }
    }

    var checkboxes = document.getElementsByClassName('table-checkbox');
    var emailsToAdd = [];
    for (var index = 0; index < checkboxes.length; index++) {
        if (checkboxes[index].checked) {
            var requiredClass = checkboxes[index].parentElement.parentElement.parentElement.parentElement.classList;
            var elems = document.getElementsByClassName(requiredClass.toString());
            var emailToAdd = elems.item(elems.length - 1).firstChild.value;
            emailsToAdd.push(emailToAdd);
        }
    }

    for (var i = 0; i < emailsToAdd.length; ++i) {
        var data;
        data = data + "##" + emailsToAdd[i];
    }

    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;

    // Turn the data object into an array of URL-encoded key/value pairs.
    urlEncodedDataPairs.push(encodeURIComponent('preSets') + '=' + encodeURIComponent(checkPreSets));
    urlEncodedDataPairs.push(encodeURIComponent('domens') + '=' + encodeURIComponent(data));

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    // Set up our request
    XHR.open('POST', '/addUserToPresetLive');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    $("#add-form").submit();

})

$("#add-domen").click(function () {
    var checkBoxes = getCheckedBoxes("domenForm");
    var checkBoxesDomens = "";
    var flag = false;
    for (var i = 0; i < checkBoxes.length; i++) {
        if (flag) {
            checkBoxesDomens = checkBoxesDomens + "!" + (checkBoxes[i].value);
        } else {
            checkBoxesDomens = checkBoxes[i].value;
            flag = !flag;
        }

    }
    flag = false;
    var checkBoxesPreSets = getCheckedBoxes("preSetForm");
    var checkPreSets = "";
    for (var i = 0; i < checkBoxesPreSets.length; i++) {
        if (flag) {
            checkPreSets = checkPreSets + "!" + (checkBoxesPreSets[i].value);
        } else {
            checkPreSets = checkBoxesPreSets[i].value;
            flag = !flag;
        }
    }

    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;

    // Turn the data object into an array of URL-encoded key/value pairs.
    urlEncodedDataPairs.push(encodeURIComponent('preSets') + '=' + encodeURIComponent(checkPreSets));
    urlEncodedDataPairs.push(encodeURIComponent('domens') + '=' + encodeURIComponent(checkBoxesDomens));

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    // Set up our request
    XHR.open('POST', '/getData');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    $("#add-form").submit();
})

function deleteDomen() {
    var checkBoxes = getCheckedBoxes("domenForm");
    var checkBoxesDomens = "";
    for (var i = 0; i < checkBoxes.length; i++) {
        checkBoxesDomens = checkBoxesDomens + " " + checkBoxes[i].value;
    }

    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;
    urlEncodedDataPairs.push(encodeURIComponent('domens') + '=' + encodeURIComponent(checkBoxesDomens));

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    // Set up our request
    XHR.open('POST', '/deleteDomen');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    $("#add-form").submit();
}

function dosomething(element) {
    alert(element.value);
}

function deletePreSet() {

    var checkBoxes = getCheckedBoxes("preSetForm");
    var checkBoxesDomens = "";
    for (var i = 0; i < checkBoxes.length; i++) {
        checkBoxesDomens = checkBoxesDomens + " " + checkBoxes[i].value;
    }

    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;
    urlEncodedDataPairs.push(encodeURIComponent('presets') + '=' + encodeURIComponent(checkBoxesDomens));

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    // Set up our request
    XHR.open('POST', '/deletePreset');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    $("#add-form").submit();
}

var table = document.getElementById("main-table");
var r = 0;
while (row = table.rows[r++]) {
    var c = 0;
    // row.className = r;
    while (cell = row.cells[c++]) {
        cell.className = r;
    }
}
;

$("#save").click(function () {
    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;
    urlEncodedDataPairs.push(encodeURIComponent('presets') + '=' + encodeURIComponent("dd"));

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });

    // Set up our request
    XHR.open('POST', '/save');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
});

document.getElementById("confirm-popup-btn-delete").addEventListener("click", function () {
    var checkboxes = document.getElementsByClassName('table-checkbox');
    var emailsToDelete = [];
    for (var index = 0; index < checkboxes.length; index++) {
        if (checkboxes[index].checked) {
            // alert(checkboxes[index].parentElement.parentElement.parentElement.parentElement.classList);
            var requiredClass = checkboxes[index].parentElement.parentElement.parentElement.parentElement.classList;
            var elems = document.getElementsByClassName(requiredClass.toString());
            var emailToDelete = elems.item(elems.length - 1).firstChild.value;
            emailsToDelete.push(emailToDelete);
        }
    }

    for (var i = 0; i < emailsToDelete.length; ++i) {
        var data;
        data = data + "##" + emailsToDelete[i];
    }
    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;

    // Turn the data object into an array of URL-encoded key/value pairs.
    urlEncodedDataPairs.push(encodeURIComponent('emailsToDelete') + '=' + encodeURIComponent(data));

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });
    // Set up our request
    XHR.open('POST', '/liveDelete');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    document.getElementsByClassName("popup-delete")[0].classList.remove("active");
    $("#add-form").submit();
});


document.getElementById("delete-preset").addEventListener("click", function () {
    document.getElementsByClassName("popup-preset")[0].classList.add("active");
});
document.getElementById("dismiss-popup-btn-preset").addEventListener("click", function () {
    document.getElementsByClassName("popup-preset")[0].classList.remove("active");
});
document.getElementById("confirm-popup-btn-preset").addEventListener("click", function () {
    deletePreSet();
    document.getElementsByClassName("popup-preset")[0].classList.remove("active");
});


document.getElementById("delete-domen").addEventListener("click", function () {
    document.getElementsByClassName("popup-domen")[0].classList.add("active");
});
document.getElementById("dismiss-popup-btn-domen").addEventListener("click", function () {
    document.getElementsByClassName("popup-domen")[0].classList.remove("active");
});
document.getElementById("confirm-popup-btn-domen").addEventListener("click", function () {
    deleteDomen();
    document.getElementsByClassName("popup-domen")[0].classList.remove("active");
});

document.getElementById("export-button").addEventListener("click", function () {
    document.getElementsByClassName("popup-export")[0].classList.add("active");
});
document.getElementById("dismiss-popup-btn-export").addEventListener("click", function () {
    document.getElementsByClassName("popup-export")[0].classList.remove("active");
});
document.getElementById("confirm-popup-btn-export").addEventListener("click", function () {
    document.getElementsByClassName("popup-export")[0].classList.remove("active");
});
document.getElementById("live-edit-button-delete").addEventListener("click", function () {
    document.getElementsByClassName("popup-delete")[0].classList.add("active");
})
document.getElementById("live-edit-button-change").addEventListener("click", function () {
    document.getElementsByClassName("popup-edit")[0].classList.add("active");
})
document.getElementById("live-edit-button-change").addEventListener("click", function () {
    document.getElementsByClassName("popup-edit")[0].classList.add("active");
})
document.getElementById("dismiss-popup-btn-edit").addEventListener("click", function () {
    document.getElementsByClassName("popup-edit")[0].classList.remove("active");
});
document.getElementById("dismiss-popup-btn-delete").addEventListener("click", function () {
    document.getElementsByClassName("popup-delete")[0].classList.remove("active");
});

document.getElementById("cancel-button").addEventListener("click", function () {
    $("#cancel-form").submit();
});

$("#confirm-popup-btn-edit").click(function () {
    var checkboxes = document.getElementsByClassName('table-checkbox');
    var dataToEdit = [];
    for (var index = 0; index < checkboxes.length; index++) {
        var data = "";
        if (checkboxes[index].checked) {
            // alert(checkboxes[index].parentElement.parentElement.parentElement.parentElement.classList);
            var requiredClass = checkboxes[index].parentElement.parentElement.parentElement.parentElement.classList;
            var elems = document.getElementsByClassName(requiredClass.toString());
            for (var indexS = 0; indexS < elems.length; indexS++) {
                data = data + "##" + elems[indexS].firstChild.value;
            }
            dataToEdit.push(data);
        }
    }
    var data = "";
    for (var index = 0; index < dataToEdit.length; index++) {
        data = data + "@@@" + dataToEdit[index];
    }
    const XHR = new XMLHttpRequest();

    let urlEncodedData = "",
        urlEncodedDataPairs = [],
        name;

    // Turn the data object into an array of URL-encoded key/value pairs.
    urlEncodedDataPairs.push(encodeURIComponent('stringToEdit') + '=' + encodeURIComponent(data));

    // Combine the pairs into a single string and replace all %-encoded spaces to
    // the '+' character; matches the behavior of browser form submissions.
    urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
    // Define what happens on successful data submission
    XHR.addEventListener('load', function (event) {
        // alert('Yeah! Data sent and response loaded.');
    });

    // Define what happens in case of error
    XHR.addEventListener('error', function (event) {
        alert('Oops! Something went wrong.');
    });
    // Set up our request
    XHR.open('POST', '/liveEdit');

    // Add the required HTTP header for form data POST requests
    XHR.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    // Finally, send our data.
    XHR.send(urlEncodedData);
    document.getElementsByClassName("popup-edit")[0].classList.remove("active");
    $("#add-form").submit();
})










