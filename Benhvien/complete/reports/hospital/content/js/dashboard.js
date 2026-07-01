/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [1.0, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Danh sach benh an"], "isController": false}, {"data": [1.0, 500, 1500, "Them benh an"], "isController": false}, {"data": [1.0, 500, 1500, "Dashboard"], "isController": false}, {"data": [1.0, 500, 1500, "Danh sach lich cap thuoc"], "isController": false}, {"data": [1.0, 500, 1500, "Chatbot"], "isController": false}, {"data": [1.0, 500, 1500, "Them benh nhan"], "isController": false}, {"data": [1.0, 500, 1500, "Danh sach benh nhan"], "isController": false}, {"data": [1.0, 500, 1500, "Them lich cap thuoc"], "isController": false}, {"data": [1.0, 500, 1500, "Get CSRF Token"], "isController": false}, {"data": [1.0, 500, 1500, "Danh sach phong"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin-0"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin - setup"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin-1"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin - setup-0"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin - setup-1"], "isController": false}, {"data": [1.0, 500, 1500, "Them phong"], "isController": false}, {"data": [1.0, 500, 1500, "Chatbot-1"], "isController": false}, {"data": [1.0, 500, 1500, "Chatbot-0"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2220, 0, 0.0, 11.245045045045032, 1, 142, 7.5, 25.0, 34.0, 53.0, 28.19978659620954, 666.339591087216, 7.824507932777806], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Danh sach benh an", 50, 0, 0.0, 31.88, 14, 95, 27.5, 53.0, 73.4499999999999, 95.0, 5.089058524173028, 468.82454675572524, 0.8746819338422392], "isController": false}, {"data": ["Them benh an", 50, 0, 0.0, 30.679999999999996, 15, 93, 22.0, 55.9, 80.24999999999997, 93.0, 5.107252298263534, 471.5770436989275, 2.1179136874361597], "isController": false}, {"data": ["Dashboard", 50, 0, 0.0, 4.680000000000001, 2, 8, 4.5, 6.0, 8.0, 8.0, 5.100479445067837, 50.16799704172192, 0.8915877154952565], "isController": false}, {"data": ["Danh sach lich cap thuoc", 50, 0, 0.0, 40.660000000000004, 25, 57, 42.5, 52.0, 55.0, 57.0, 5.082333807684488, 507.4094868113438, 0.8834525564139053], "isController": false}, {"data": ["Chatbot", 30, 0, 0.0, 7.3999999999999995, 4, 13, 6.5, 11.900000000000002, 13.0, 13.0, 3.103020273065784, 17.969638886015723, 1.709085384774514], "isController": false}, {"data": ["Them benh nhan", 50, 0, 0.0, 25.08, 11, 142, 21.0, 33.9, 39.14999999999997, 142.0, 5.117183502200389, 510.37818784540985, 2.0061957898372733], "isController": false}, {"data": ["Danh sach benh nhan", 100, 0, 0.0, 22.089999999999996, 12, 68, 20.0, 31.900000000000006, 35.0, 67.68999999999984, 10.024057738572575, 997.6776294356455, 1.7424631615878106], "isController": false}, {"data": ["Them lich cap thuoc", 50, 0, 0.0, 33.94000000000001, 24, 49, 33.5, 42.0, 45.449999999999996, 49.0, 5.078204346942921, 508.1170930771379, 2.0653017405545397], "isController": false}, {"data": ["Get CSRF Token", 430, 0, 0.0, 9.730232558139532, 3, 58, 8.0, 16.0, 18.44999999999999, 39.379999999999995, 5.462675948981147, 27.681470214124197, 0.6731576966563342], "isController": false}, {"data": ["Danh sach phong", 50, 0, 0.0, 13.24, 7, 29, 12.0, 18.9, 20.89999999999999, 29.0, 5.0864699898270604, 342.3730766785351, 0.864302517802645], "isController": false}, {"data": ["Login Admin-0", 50, 0, 0.0, 5.32, 2, 12, 5.0, 8.0, 9.0, 12.0, 5.175447676223993, 4.341513236207432, 1.6830313243970605], "isController": false}, {"data": ["Login Admin - setup", 350, 0, 0.0, 10.43714285714286, 4, 63, 9.0, 15.0, 18.0, 41.7800000000002, 5.906774226212576, 63.0536611979782, 2.953387113106288], "isController": false}, {"data": ["Login Admin", 50, 0, 0.0, 11.319999999999999, 6, 19, 11.0, 16.0, 17.89999999999999, 19.0, 5.172235440157236, 55.21260312144409, 2.586117720078618], "isController": false}, {"data": ["Login Admin-1", 50, 0, 0.0, 5.679999999999999, 3, 11, 5.0, 9.899999999999999, 10.0, 11.0, 5.176519308417021, 50.915920385133035, 0.9048798400455533], "isController": false}, {"data": ["Login Admin - setup-0", 350, 0, 0.0, 4.965714285714284, 1, 34, 4.0, 8.0, 9.0, 24.920000000000073, 5.9071729957805905, 4.955333597046414, 1.9209849683544304], "isController": false}, {"data": ["Login Admin - setup-1", 350, 0, 0.0, 5.194285714285712, 2, 29, 5.0, 8.0, 10.0, 20.49000000000001, 5.907272696585596, 58.103565039072386, 1.0326189577039275], "isController": false}, {"data": ["Them phong", 50, 0, 0.0, 15.940000000000003, 9, 33, 15.0, 22.0, 27.449999999999996, 33.0, 5.084918132818062, 343.315497718143, 1.7660159030814604], "isController": false}, {"data": ["Chatbot-1", 30, 0, 0.0, 3.766666666666666, 2, 8, 3.5, 6.0, 7.449999999999999, 8.0, 3.1039834454216244, 15.459292550439732, 0.7850895628556649], "isController": false}, {"data": ["Chatbot-0", 30, 0, 0.0, 3.5333333333333328, 2, 8, 3.0, 6.0, 7.449999999999999, 8.0, 3.1039834454216244, 2.515924081738231, 0.9245263191929644], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 2220, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
