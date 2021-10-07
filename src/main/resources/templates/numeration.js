var table = document.getElementById("main-table");
alert("dfdf")
while(row=table.rows[r++])
{
    var c=0;
    while(cell=row.cells[c++])
    {
        cell.innerHTML='[Row='+r+',Col='+c+']'; // do sth with cell
    }
}

