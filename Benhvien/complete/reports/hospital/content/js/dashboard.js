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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [1.0, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "Danh sach benh an"], "isController": false}, {"data": [1.0, 500, 1500, "Them benh an"], "isController": false}, {"data": [1.0, 500, 1500, "Dashboard"], "isController": false}, {"data": [1.0, 500, 1500, "Danh sach lich cap thuoc"], "isController": false}, {"data": [1.0, 500, 1500, "Chatbot"], "isController": false}, {"data": [1.0, 500, 1500, "Them benh nhan"], "isController": false}, {"data": [1.0, 500, 1500, "Danh sach benh nhan"], "isController": false}, {"data": [1.0, 500, 1500, "Them lich cap thuoc"], "isController": false}, {"data": [1.0, 500, 1500, "Danh sach phong"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin-0"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin - setup"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin-1"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin - setup-0"], "isController": false}, {"data": [1.0, 500, 1500, "Login Admin - setup-1"], "isController": false}, {"data": [1.0, 500, 1500, "Them phong"], "isController": false}, {"data": [1.0, 500, 1500, "Chatbot-1"], "isController": false}, {"data": [1.0, 500, 1500, "Chatbot-0"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 1790, 0, 0.0, 18.784357541899432, 1, 167, 7.0, 57.0, 76.0, 103.08999999999992, 22.71198913884765, 609.5658443483309, 6.364575997931813], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Danh sach benh an", 50, 0, 0.0, 59.8, 31, 98, 57.0, 88.8, 90.44999999999999, 98.0, 5.076142131979695, 461.5720177664975, 0.8724619289340102], "isController": false}, {"data": ["Them benh an", 50, 0, 0.0, 49.900000000000006, 37, 75, 48.0, 67.8, 73.35, 75.0, 5.090094675760969, 463.91351531482235, 1.8970544258373205], "isController": false}, {"data": ["Dashboard", 50, 0, 0.0, 4.84, 1, 14, 4.0, 7.0, 11.249999999999979, 14.0, 5.100999795960008, 46.342383888492144, 0.891678675270353], "isController": false}, {"data": ["Danh sach lich cap thuoc", 50, 0, 0.0, 92.26000000000002, 63, 167, 83.5, 131.49999999999997, 144.45, 167.0, 5.063803929511849, 500.3720709059145, 0.8802315424346768], "isController": false}, {"data": ["Chatbot", 30, 0, 0.0, 8.299999999999999, 3, 20, 7.5, 12.0, 17.249999999999996, 20.0, 3.102699348433137, 13.331911262798634, 1.0726128606887992], "isController": false}, {"data": ["Them benh nhan", 50, 0, 0.0, 56.779999999999994, 27, 95, 56.5, 88.9, 92.89999999999999, 95.0, 5.064316823660488, 485.6888539134509, 1.7678620036969515], "isController": false}, {"data": ["Danh sach benh nhan", 100, 0, 0.0, 38.76999999999999, 18, 77, 37.0, 55.900000000000006, 62.0, 76.97999999999999, 10.079629069650236, 964.5634103165004, 1.7521230218727952], "isController": false}, {"data": ["Them lich cap thuoc", 50, 0, 0.0, 83.31999999999998, 70, 130, 79.0, 100.6, 120.39999999999995, 130.0, 5.063803929511849, 501.4887781357606, 1.8468048979643508], "isController": false}, {"data": ["Danh sach phong", 50, 0, 0.0, 38.64, 16, 89, 33.0, 69.19999999999999, 76.35, 89.0, 5.083884087442806, 327.92045310116924, 0.8638631164209455], "isController": false}, {"data": ["Login Admin-0", 50, 0, 0.0, 6.2, 2, 34, 6.0, 8.899999999999999, 13.449999999999996, 34.0, 5.140331037318803, 1.5461151948185463, 1.1897055232856995], "isController": false}, {"data": ["Login Admin - setup", 350, 0, 0.0, 10.131428571428573, 3, 36, 9.0, 17.0, 19.0, 27.0, 5.909965891054, 55.46941619035156, 2.654867490121914], "isController": false}, {"data": ["Login Admin", 50, 0, 0.0, 13.940000000000003, 4, 42, 12.0, 23.799999999999997, 33.79999999999998, 42.0, 5.125576627370579, 48.10734078677601, 2.3025051255766273], "isController": false}, {"data": ["Login Admin-1", 50, 0, 0.0, 6.98, 2, 26, 6.0, 12.899999999999999, 16.449999999999996, 26.0, 5.145091582630171, 46.742956048055156, 1.1204642802016875], "isController": false}, {"data": ["Login Admin - setup-0", 350, 0, 0.0, 4.5657142857142885, 1, 24, 4.0, 8.0, 9.0, 12.0, 5.910065686158626, 1.7776369446648994, 1.3678569996285102], "isController": false}, {"data": ["Login Admin - setup-1", 350, 0, 0.0, 5.179999999999996, 1, 25, 5.0, 9.0, 11.0, 15.490000000000009, 5.910664527569028, 53.69815634763151, 1.2871857320780207], "isController": false}, {"data": ["Them phong", 50, 0, 0.0, 33.239999999999995, 21, 57, 31.0, 47.699999999999996, 54.24999999999998, 57.0, 5.073566717402334, 328.3044576040081, 1.5490233384068999], "isController": false}, {"data": ["Chatbot-1", 30, 0, 0.0, 4.2666666666666675, 2, 10, 3.5, 7.900000000000002, 9.45, 10.0, 3.1039834454216244, 12.852431453698914, 0.4668100103466115], "isController": false}, {"data": ["Chatbot-0", 30, 0, 0.0, 3.9333333333333322, 1, 9, 4.0, 6.900000000000002, 7.899999999999999, 9.0, 3.105590062111801, 0.48524844720496896, 0.6065605590062112], "isController": false}]}, function(index, item){
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
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 1790, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
