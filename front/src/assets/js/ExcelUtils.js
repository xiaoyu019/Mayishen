import * as XLSX from "xlsx";
/*
    * @description: 导出EXCEL
    * @param {Object} json 服务端发过来的数据
    * @param {String} name 导出Excel文件名字
    * @param {Array} header 导出Excel表头
    * @param {Array} widthArr 各列宽度
    * @param {String} sheetName 导出sheetName名字
    * @return:
    */
export function exportExcel(json, name, header, widthArr, sheetName) {
    /* convert state to workbook */
    var data = new Array();
    var keyArray = new Array();
    const getLength = function (obj) {
        var count = 0;
        for (var i in obj) {
            if (obj.hasOwnProperty(i)) {
                count++;
            }
        }
        return count;
    };
    for (const key1 in json) {
        if (json.hasOwnProperty(key1)) {
            const element = json[key1];
            var rowDataArray = new Array();
            for (const key2 in header) {
                if (element.hasOwnProperty(key2)) {
                    const element2 = element[key2];
                    rowDataArray.push(element2);
                    if (keyArray.length < getLength(header)) {
                        keyArray.push(header[key2]);
                    }
                }
            }
            data.push(rowDataArray);
        }
    }
    data.splice(0, 0, keyArray);
    const ws = XLSX.utils.aoa_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, sheetName);
    // // 此处隐藏英文字段表头
    // var wsrows = [{ hidden: true }];
    // ws['!rows'] = wsrows; // ws - worksheet
    console.log(widthArr)
    wb['Sheets'][sheetName]['!cols']=[]
    for(let i=0;i<widthArr.length;i++){
        wb['Sheets'][sheetName]['!cols'][i]={wpx: widthArr[i]};
    }
    console.log( wb['Sheets'][sheetName])
    /* generate file and send to client */
    XLSX.writeFile(wb, name + '.xlsx');
}

export const readExcel = (file) => {
    return new Promise(resolve => {
        let reader = new FileReader()
        reader.readAsBinaryString(file)
        reader.onload = ev => {
            resolve(ev.target.result)
        }
    })
}