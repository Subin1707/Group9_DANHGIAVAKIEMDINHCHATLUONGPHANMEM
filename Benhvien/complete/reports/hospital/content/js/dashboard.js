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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2220, 0, 0.0, 11.47207207207208, 1, 496, 8.0, 21.0, 28.0, 43.789999999999964, 28.24032260116269, 667.2974261712737, 7.835755333223085], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Danh sach benh an", 50, 0, 0.0, 20.04, 13, 42, 19.0, 25.799999999999997, 35.24999999999998, 42.0, 5.085952598921778, 468.53841642508394, 0.8741481029396806], "isController": false}, {"data": ["Them benh an", 50, 0, 0.0, 18.840000000000003, 13, 34, 17.0, 24.9, 28.799999999999983, 34.0, 5.094762584063583, 470.42380862161195, 2.1127343590788668], "isController": false}, {"data": ["Dashboard", 50, 0, 0.0, 8.620000000000001, 3, 101, 6.0, 13.0, 17.24999999999998, 101.0, 5.094762584063583, 50.11176635418789, 0.890588381393927], "isController": false}, {"data": ["Danh sach lich cap thuoc", 50, 0, 0.0, 33.13999999999999, 23, 90, 30.0, 43.0, 55.24999999999998, 90.0, 5.085952598921778, 507.770779295087, 0.8840816041094497], "isController": false}, {"data": ["Chatbot", 30, 0, 0.0, 9.299999999999999, 4, 15, 10.0, 13.0, 13.899999999999999, 15.0, 3.10494721589733, 17.980797842061683, 1.710146708755951], "isController": false}, {"data": ["Them benh nhan", 50, 0, 0.0, 21.039999999999996, 13, 38, 20.0, 29.9, 32.449999999999996, 38.0, 5.093724531377343, 508.0384345392726, 1.9969986819987773], "isController": false}, {"data": ["Danh sach benh nhan", 100, 0, 0.0, 31.54999999999999, 11, 476, 18.0, 29.900000000000006, 50.29999999999984, 475.0099999999995, 10.095911155981828, 1004.8290793791015, 1.7549533064109037], "isController": false}, {"data": ["Them lich cap thuoc", 50, 0, 0.0, 29.359999999999996, 21, 45, 29.0, 36.0, 40.699999999999974, 45.0, 5.088540606554041, 509.1513228615408, 2.0695054892631792], "isController": false}, {"data": ["Get CSRF Token", 430, 0, 0.0, 11.430232558139537, 4, 496, 9.0, 15.0, 18.0, 53.039999999999964, 5.470250741028153, 27.71985458515145, 0.6740911273805132], "isController": false}, {"data": ["Danh sach phong", 50, 0, 0.0, 14.139999999999997, 9, 24, 13.0, 19.9, 22.89999999999999, 24.0, 5.098399102681758, 343.1760317885184, 0.8663295350260019], "isController": false}, {"data": ["Login Admin-0", 50, 0, 0.0, 10.1, 3, 153, 5.0, 12.0, 42.29999999999977, 153.0, 5.412426932236414, 4.540307358194415, 1.7600958676120373], "isController": false}, {"data": ["Login Admin - setup", 350, 0, 0.0, 9.988571428571417, 4, 40, 9.0, 15.0, 16.0, 21.49000000000001, 5.920863431055775, 63.20406070788152, 2.9604317155278874], "isController": false}, {"data": ["Login Admin", 50, 0, 0.0, 19.279999999999994, 7, 219, 12.0, 22.599999999999994, 80.69999999999955, 219.0, 5.407159078620093, 57.72036707851195, 2.7035795393100464], "isController": false}, {"data": ["Login Admin-1", 50, 0, 0.0, 8.780000000000001, 3, 65, 6.0, 9.0, 37.04999999999979, 65.0, 5.499340079190497, 54.09116531016278, 0.96131042399912], "isController": false}, {"data": ["Login Admin - setup-0", 350, 0, 0.0, 4.451428571428573, 1, 24, 4.0, 7.0, 8.0, 10.490000000000009, 5.921364282330648, 4.967238201681668, 1.9255999082188537], "isController": false}, {"data": ["Login Admin - setup-1", 350, 0, 0.0, 5.317142857142863, 2, 16, 5.0, 8.0, 9.449999999999989, 14.490000000000009, 5.921163931652851, 58.24019835899171, 1.035047210708848], "isController": false}, {"data": ["Them phong", 50, 0, 0.0, 15.200000000000001, 11, 34, 13.0, 22.699999999999996, 31.89999999999999, 34.0, 5.100999795960008, 344.4012741341053, 1.7716011400734544], "isController": false}, {"data": ["Chatbot-1", 30, 0, 0.0, 5.133333333333332, 2, 9, 5.0, 7.0, 8.45, 9.0, 3.1062331745703045, 15.470497256160696, 0.7856585861462001], "isController": false}, {"data": ["Chatbot-0", 30, 0, 0.0, 4.1000000000000005, 1, 8, 4.0, 6.0, 7.449999999999999, 8.0, 3.105590062111801, 2.5172263198757765, 0.9250048524844721], "isController": false}]}, function(index, item){
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
