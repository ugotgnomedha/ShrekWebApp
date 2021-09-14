
var start = "<th scope=\"col\">";
var end = "</th>-->";

fs.readFile('D:\\Files\\Programming\\projects\\JavaProgramming\\ShrekWebApp\\Headers.txt', 'utf-8', (err, data) => {
    if (err) throw err;

    console.log(data);
})
document.getElementById("table-names").innerHTML +=
    start +"dfd" +end;