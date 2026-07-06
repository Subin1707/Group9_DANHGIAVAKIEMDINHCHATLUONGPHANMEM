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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2220, 0, 0.0, 17.8644144144144, 6, 68, 16.0, 29.0, 35.0, 42.0, 28.15008305542523, 665.1651340774507, 7.81071684440105], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Danh sach benh an", 50, 0, 0.0, 22.919999999999998, 18, 42, 22.0, 28.9, 30.0, 42.0, 5.08130081300813, 468.1098751905488, 0.8733485772357724], "isController": false}, {"data": ["Them benh an", 50, 0, 0.0, 26.920000000000005, 21, 39, 26.0, 31.0, 35.449999999999996, 39.0, 5.076142131979695, 468.704493178934, 2.10501269035533], "isController": false}, {"data": ["Dashboard", 50, 0, 0.0, 12.120000000000003, 9, 26, 12.0, 14.0, 16.799999999999983, 26.0, 5.085435313262815, 50.02002390154597, 0.888957930736371], "isController": false}, {"data": ["Danh sach lich cap thuoc", 50, 0, 0.0, 37.620000000000005, 30, 56, 37.0, 42.9, 48.449999999999996, 56.0, 5.082333807684488, 507.4094868113438, 0.8834525564139053], "isController": false}, {"data": ["Chatbot", 30, 0, 0.0, 21.733333333333334, 16, 41, 20.0, 35.500000000000014, 38.8, 41.0, 3.084198622391282, 17.86064241287139, 1.6987187724889483], "isController": false}, {"data": ["Them benh nhan", 50, 0, 0.0, 27.44, 22, 42, 27.0, 33.0, 35.449999999999996, 42.0, 5.092686901609289, 507.93494331202896, 1.9965918784375638], "isController": false}, {"data": ["Danh sach benh nhan", 100, 0, 0.0, 24.300000000000004, 16, 63, 22.5, 32.900000000000006, 34.94999999999999, 62.77999999999989, 10.07759750075582, 1003.0063520356747, 1.7517698780610702], "isController": false}, {"data": ["Them lich cap thuoc", 50, 0, 0.0, 38.680000000000014, 30, 51, 38.0, 43.0, 46.449999999999996, 51.0, 5.085952598921778, 508.89237091216563, 2.0684529485810192], "isController": false}, {"data": ["Get CSRF Token", 430, 0, 0.0, 17.020930232558147, 10, 68, 16.0, 24.0, 28.0, 40.06999999999999, 5.455122105930859, 27.64319199968284, 0.672226847446876], "isController": false}, {"data": ["Danh sach phong", 50, 0, 0.0, 19.04000000000001, 14, 38, 18.0, 24.0, 27.89999999999999, 38.0, 5.090094675760969, 342.6170562709966, 0.8649184312328209], "isController": false}, {"data": ["Login Admin-0", 50, 0, 0.0, 11.459999999999997, 7, 23, 11.0, 15.899999999999999, 20.449999999999996, 23.0, 5.1964248596965295, 4.359110307108709, 1.6898530061317814], "isController": false}, {"data": ["Login Admin - setup", 350, 0, 0.0, 22.611428571428572, 14, 65, 21.0, 28.900000000000034, 32.0, 41.450000000000045, 5.900401227283456, 62.985630679136186, 2.950200613641728], "isController": false}, {"data": ["Login Admin", 50, 0, 0.0, 25.059999999999995, 18, 52, 23.0, 32.0, 39.849999999999945, 52.0, 5.189952252439277, 55.401726632239985, 2.5949761262196387], "isController": false}, {"data": ["Login Admin-1", 50, 0, 0.0, 13.36, 9, 30, 12.5, 18.0, 22.89999999999999, 30.0, 5.201831044527673, 51.164885039533914, 0.9093044501664586], "isController": false}, {"data": ["Login Admin - setup-0", 350, 0, 0.0, 10.417142857142862, 6, 33, 10.0, 14.0, 16.0, 21.470000000000027, 5.9014956076011265, 4.95057102239196, 1.9191387083312257], "isController": false}, {"data": ["Login Admin - setup-1", 350, 0, 0.0, 12.014285714285709, 7, 32, 11.0, 15.0, 17.0, 23.49000000000001, 5.901197099983139, 58.043805850615406, 1.031556914938459], "isController": false}, {"data": ["Them phong", 50, 0, 0.0, 22.24, 18, 31, 21.0, 26.799999999999997, 29.449999999999996, 31.0, 5.085435313262815, 343.3504158932567, 1.7661955222742065], "isController": false}, {"data": ["Chatbot-1", 30, 0, 0.0, 11.066666666666665, 8, 18, 10.0, 15.900000000000002, 17.45, 18.0, 3.0867373186541824, 15.373398755015948, 0.7807275054017904], "isController": false}, {"data": ["Chatbot-0", 30, 0, 0.0, 10.600000000000001, 7, 23, 9.0, 19.400000000000013, 21.9, 23.0, 3.089280197713933, 2.50400641025641, 0.9201469338894037], "isController": false}]}, function(index, item){
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
