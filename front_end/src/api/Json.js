import MyAxios from "@/api/MyAxios";

export function getVal(jsonStr, sqlForm) {
    return MyAxios({
        url: 'api/json/getValue',
        method: 'post',
        data: {
            jsonStr: JSON.stringify(jsonStr),
            sqlForm,
        }
    })
};

export function getObjKeys(jsonStr) {
    return MyAxios({
        url: 'api/json/getKeys',
        method: 'post',
        data: jsonStr
    })
}


export function getChildKeys(jsonStr, needKey) {
    return MyAxios({
        url: 'api/json/getChildKeys/' + needKey,
        method: 'post',
        data: jsonStr
    })
}