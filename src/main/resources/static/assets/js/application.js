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
function getCheckedBoxes(chkboxName) {
    var checkboxes = document.getElementById(chkboxName);
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

$("#delete-domen").click(function () {
    var checkBoxes = getCheckedBoxes("domenForm");
    var checkBoxesDomens = "";
    for (var i = 0; i < checkBoxes.length; i++) {
        checkBoxesDomens = checkBoxes[i].value;
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
})

function dosomething(element) {
    alert(element.value);
}

function deletePreSet() {

    var checkBoxes = getCheckedBoxes("preSetForm");
    var checkBoxesDomens = "";
    for (var i = 0; i < checkBoxes.length; i++) {
        checkBoxesDomens = checkBoxes[i].value;
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


$(".eButton").click(function () {
    var myClass = $(this).parent().attr("class");

    var cusid_ele = $("." + myClass)
    var data = "";
    for (var i = 0; i < cusid_ele.length; ++i) {
        var item = cusid_ele[i];
        // $(item).children(':first').attr("value", "new value");// transforming to jquery item
        if (i == 0) {
            data = $(item).children(':first').val();
        } else {
            data = data + "##" + $(item).children(':first').val();
        }

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
    $("#add-form").submit();

});

document.getElementById("delete-preset").addEventListener("click", function () {
    document.getElementsByClassName("popup")[0].classList.add("active");
});

document.getElementById("dismiss-popup-btn").addEventListener("click", function () {
    document.getElementsByClassName("popup")[0].classList.remove("active");
});
document.getElementById("confirm-popup-btn").addEventListener("click", function () {
    deletePreSet();
    document.getElementsByClassName("popup")[0].classList.remove("active");
});





