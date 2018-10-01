package main

import (
    "fmt"
    "bytes"
    "strconv"
    "time"
    "strings"

    "github.com/hyperledger/fabric/core/chaincode/shim"
    "github.com/hyperledger/fabric/protos/peer"
)

// SimpleAsset implements a simple chaincode to manage an asset
type SimpleAsset struct {
}

// Init is called during chaincode instantiation to initialize any
// data. Note that chaincode upgrade also calls this function to reset
// or to migrate data.
func (t *SimpleAsset) Init(stub shim.ChaincodeStubInterface) peer.Response {
    // Get the args from the transaction proposal
    args := stub.GetStringArgs()
    if len(args) != 2 {
        return shim.Error("Incorrect arguments. Expecting a key and a value")
    }

    // Set up any variables or assets here by calling stub.PutState()

    // We store the key and the value on the ledger
    err := stub.PutState(args[0], []byte(args[1]))
    if err != nil {
        return shim.Error(fmt.Sprintf("Failed to create asset: %s", args[0]))
    }
    return shim.Success(nil)
}

// Invoke is called per transaction on the chaincode. Each transaction is
// either a 'get' or a 'set' on the asset created by Init function. The Set
// method may create a new asset by specifying a new key-value pair.
func (t *SimpleAsset) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
    // Extract the function and args from the transaction proposal
    fn, args := stub.GetFunctionAndParameters()

    var result string
    var err error
    if fn == "set" {
        result, err = set(stub, args)
    } else if fn == "history" {
        result, err = getHistory(stub, args)
    } else if fn == "get"{
        result, err = get(stub, args)
    } else if fn == "range"{
        result, err = getStateByRange(stub, args)
    } else if fn == "match"{
        result, err = doMatch(stub, args)
    } else {
        return shim.Error(fmt.Sprintf("Failed to get func: %s ", fn))
    }

    if err != nil {
        result, err = "", fmt.Errorf("Failed to get func: %s ", fn)
    }

    // Return the result as success payload
    return shim.Success([]byte(result))
}

// Set stores the asset (both key and value) on the ledger. If the key exists,
// it will override the value with the new one
func set(stub shim.ChaincodeStubInterface, args []string) (string, error) {
    if len(args) != 2 {
        return "", fmt.Errorf("Incorrect arguments. Expecting a key and a value")
    }

    err := stub.PutState(args[0], []byte(args[1]))
    if err != nil {
        return "", fmt.Errorf("Failed to set asset: %s", args[0])
    }
    return args[1], nil
}

// Get returns the value of the specified asset key
func get(stub shim.ChaincodeStubInterface, args []string) (string, error) {
    if len(args) != 1 {
        return "", fmt.Errorf("Incorrect arguments. Expecting a key")
    }

    value, err := stub.GetState(args[0])
    if err != nil {
        return "", fmt.Errorf("Failed to get asset: %s with error: %s", args[0], err)
    }
    if value == nil {
        return "", fmt.Errorf("Asset not found: %s", args[0])
    }
    return string(value), nil
}

func getHistory(stub shim.ChaincodeStubInterface, args []string) (string, error){
    if len(args) != 1 {
        return "", fmt.Errorf("Incorrect arguments. Expecting a key")
    }

    resultsIterator, err := stub.GetHistoryForKey(args[0])
    if err != nil {
        return "", fmt.Errorf("Failed to get asset: %s with error: %s", args[0], err)
    }
    defer resultsIterator.Close()

    // buffer is a JSON array containing historic values for the marble
    var buffer bytes.Buffer
    buffer.WriteString("{\"data\": [")

    bArrayMemberAlreadyWritten := false
    for resultsIterator.HasNext() {
        response, err := resultsIterator.Next()
        if err != nil {
            return "", fmt.Errorf("Failed to get asset: %s with error: %s", args[0], err)
        }
        // Add a comma before array members, suppress it for the first array member
        if bArrayMemberAlreadyWritten == true {
            buffer.WriteString(",")
        }
        buffer.WriteString("{\"txId\":")
        buffer.WriteString("\"")
        buffer.WriteString(response.TxId)
        buffer.WriteString("\"")

        buffer.WriteString(", \"collectData\":")
        // if it was a delete operation on given key, then we need to set the
        //corresponding value null. Else, we will write the response.Value
        //as-is (as the Value itself a JSON marble)
        if response.IsDelete {
            buffer.WriteString("null")
        } else {
            buffer.WriteString(string(response.Value))
        }

        buffer.WriteString(", \"timestamp\":")
        buffer.WriteString("\"")
        buffer.WriteString(time.Unix(response.Timestamp.Seconds, int64(response.Timestamp.Nanos)).String())
        buffer.WriteString("\"")

        buffer.WriteString(", \"isDelete\":")
        buffer.WriteString("\"")
        buffer.WriteString(strconv.FormatBool(response.IsDelete))
        buffer.WriteString("\"")

        buffer.WriteString("}")
        bArrayMemberAlreadyWritten = true
    }
    buffer.WriteString("]}")

    return string(buffer.Bytes()),nil
}

func getStateByRange(stub shim.ChaincodeStubInterface, args []string) (string, error){
    if len(args) != 2 {
        return "", fmt.Errorf("Incorrect arguments. Expecting a startKey and a endKey")
    }

    resultsIterator, err := stub.GetStateByRange(args[0],args[1])
    if err != nil {
        return "", fmt.Errorf("Failed to get asset: %s and %s with error: %s", args[0], args[1], err)
    }
    defer resultsIterator.Close()

    // buffer is a JSON array containing historic values for the marble
    var buffer bytes.Buffer
    buffer.WriteString("{\"data\": [")

    bArrayMemberAlreadyWritten := false
    for resultsIterator.HasNext() {
        response, err := resultsIterator.Next()
        if err != nil {
            return "", fmt.Errorf("Failed to get asset: %s and %s with error: %s", args[0], args[1], err)
        }
        // Add a comma before array members, suppress it for the first array member
        if bArrayMemberAlreadyWritten == true {
            buffer.WriteString(",")
        }
        buffer.WriteString("{\"key\":")
        buffer.WriteString("\"")
        buffer.WriteString(response.Key)
        buffer.WriteString("\"")

        buffer.WriteString(", \"collectData\":") 
        buffer.WriteString(string(response.Value))
        
        buffer.WriteString("}")
        bArrayMemberAlreadyWritten = true
    }

    buffer.WriteString("]}")

    return string(buffer.Bytes()),nil
}

const split_row = "\n"
const split_column = ","

//info.getCompanyName(), info.getSegmentName(), info.getTargetCompanyName(), matchKey
func doMatch(stub shim.ChaincodeStubInterface, args []string) (string, error){
    if len(args) < 4 {
        return "", fmt.Errorf("Incorrect arguments. Expecting targetCompanyName,companyName,segmentName and some matchKey")
    }

    // run match
    company_data, err := stub.GetState(getCompanyDataKey(args[2]))
    if err != nil {
        stub.PutState(getMatchKey(args), []byte(getSummaryError(args, "Failed to get asset: " + args[2])))
        return "", fmt.Errorf("Failed to get asset: %s with error: %s", args[2], err)
    }
    if company_data == nil {
        stub.PutState(getMatchKey(args), []byte(getSummaryError(args, "company data not found: " + args[2])))
        return "", fmt.Errorf("company data not found: %s", args[2])
    }

    segment_key := getSegmentDataKey(args[0], args[1])
    segment_data, err := stub.GetState(segment_key)
    if err != nil {
        stub.PutState(getMatchKey(args), []byte(getSummaryError(args, "company data not found: " + segment_key)))
        return "", fmt.Errorf("Failed to get asset: %s with error: %s", segment_key, err)
    }
    if segment_data == nil {
        stub.PutState(getMatchKey(args), []byte(getSummaryError(args, "segment data not found: " + segment_key)))
        return "", fmt.Errorf("segment data not found: %s", segment_key)
    }

    company_data_arr := strings.Split(string(company_data), split_row)
    company_data_head := company_data_arr[0]
    company_data_map := getHeadPosition(args, company_data_head)

    segment_data_arr := strings.Split(string(segment_data), split_row)
    segment_data_head := segment_data_arr[0]
    segment_data_map := getHeadPosition(args, segment_data_head)

    count,info := getMatchResult(args, company_data_arr, segment_data_arr, company_data_map, segment_data_map)

    if count == 0 {
        stub.PutState(getMatchKey(args), []byte(getSummaryError(args, info)))
        return "", fmt.Errorf(info)
    }

    dataKey := getMatchDataKey(args)

    // save data
    err = stub.PutState(dataKey, []byte(company_data_head + split_row + info))
    if err != nil {
        stub.PutState(getMatchKey(args), []byte(getSummaryError(args, "Failed to save match data")))
        return "", fmt.Errorf("Failed to save match data")
    }
    // save summary
    err = stub.PutState(getMatchKey(args), []byte(getSummaryInfo(args, count, dataKey,len(segment_data_arr) - 1)))
    if err != nil {
        stub.PutState(getMatchKey(args), []byte(getSummaryError(args, "Failed to save match summary")))
        return "", fmt.Errorf("Failed to save match summary")
    }

    return args[1], nil
}

func getSummaryInfo(args []string, totalCount int, dataKey string, totalSegCount int) string {
    var buffer bytes.Buffer
    buffer.WriteString("{\"segmentName\": ")
    buffer.WriteString("\"")
    buffer.WriteString(args[1])
    buffer.WriteString("\"")

    buffer.WriteString(", \"targetCompanyName\":")
    buffer.WriteString("\"")
    buffer.WriteString(args[2])
    buffer.WriteString("\"")

    buffer.WriteString(", \"matchKey\":[")
    var comma_flag = false
    for i := 3; i < len(args); i++ {
        if (comma_flag) {
            buffer.WriteString(split_column)
        }
        buffer.WriteString("\"")
        buffer.WriteString(args[i])
        buffer.WriteString("\"")
        comma_flag = true
    }
    buffer.WriteString("]")

    buffer.WriteString(", \"totalMatched\":")
    buffer.WriteString(strconv.Itoa(totalCount))

    buffer.WriteString(", \"totalSegCount\":")
    buffer.WriteString(strconv.Itoa(totalSegCount))
  
    buffer.WriteString(", \"status\":")
    buffer.WriteString("\"done\"")

    buffer.WriteString(", \"finishTime\":")
    buffer.WriteString("\"")
    buffer.WriteString(time.Now().Format("2006-01-02 15:04:05"))
    buffer.WriteString("\"")

    buffer.WriteString(", \"dataKey\":")
    buffer.WriteString("\"")
    buffer.WriteString(dataKey)
    buffer.WriteString("\"}")

   return string(buffer.Bytes())
}

func getSummaryError(args []string, error string) string {
    var buffer bytes.Buffer
    buffer.WriteString("{\"segmentName\": ")
    buffer.WriteString("\"")
    buffer.WriteString(args[1])
    buffer.WriteString("\"")

    buffer.WriteString(", \"targetCompanyName\":")
    buffer.WriteString("\"")
    buffer.WriteString(args[2])
    buffer.WriteString("\"")
 
    buffer.WriteString(", \"matchKey\":[")
    var comma_flag = false
    for i := 3; i < len(args); i++ {
        if (comma_flag) {
            buffer.WriteString(split_column)
        }
        buffer.WriteString("\"")
        buffer.WriteString(args[i])
        buffer.WriteString("\"")
        comma_flag = true
    }
    buffer.WriteString("]")

    buffer.WriteString(", \"finishTime\":")
    buffer.WriteString("\"")
    buffer.WriteString(time.Now().Format("2006-01-02 15:04:05"))
    buffer.WriteString("\"")
  
    buffer.WriteString(", \"status\":")
    buffer.WriteString("\"failed\"")

    buffer.WriteString(", \"error\":")
    buffer.WriteString("\"")
    buffer.WriteString(error)
    buffer.WriteString("\"}")

   return string(buffer.Bytes())
}

func getMatchKey(args []string) string {
   return "match_" + args[0] + "_result_" + args[1] + "_with_" + args[2];
}

func getCompanyDataKey(companyName string) string {
   return "company_data_" + companyName
}

func getSegmentDataKey(companyName string, segmentName string) string {
   return "segment_" + companyName + "_data_" + segmentName
}

func getMatchDataKey(args []string) string {
   return "match_data_" + args[0] + "_result_" + args[1] + "_with_" + args[2];
}

func getMatchResult(matchArr []string, company_data_arr []string, segment_data_arr []string, company_data_map map[string]int, segment_data_map map[string]int) (int,string) {
    result := 0
    info :=""
    for k, v := range company_data_map {
        if(v < 0) {
            return 0,"Failed to find the column in company data:" + k
        }
    }
    for k, v := range segment_data_map {
        if(v < 0) {
            return 0,"Failed to find the column in segment data:" + k
        }
    }
    
    var comma_flag = false
    for i := 1; i < len(segment_data_arr); i++ {
        count,data := getMatchResult2(matchArr, company_data_arr, segment_data_arr[i], company_data_map, segment_data_map)
        if count > 0 {
            if (comma_flag) {
                info += split_row
            }
            result ++
            info += data
            comma_flag = true
        }
    }

   return result, info;
}

func getMatchResult2(matchArr []string, company_data_arr []string, segment_data string, company_data_map map[string]int, segment_data_map map[string]int) (int,string) {
    for i := 1; i < len(company_data_arr); i++ {
        result, info := getMatchResult3(matchArr, company_data_arr[i], segment_data, company_data_map, segment_data_map)
        if result > 0 {
            return result, info;
        }
    }
   return 0, "";
}

func getMatchResult3(matchArr []string, company_data string, segment_data string, company_data_map map[string]int, segment_data_map map[string]int) (int,string) {
    var check_flag = true

    company_data_detail_arr := strings.Split(company_data, split_column)
    segment_data_detail_arr := strings.Split(segment_data, split_column)

    for i := 3; i < len(matchArr); i++ {
        if company_data_detail_arr[company_data_map[matchArr[i]]] != segment_data_detail_arr[segment_data_map[matchArr[i]]] {
            check_flag = false
        }       
    }

    if check_flag {
        // match
        return 1, company_data;
    } else {
        // not match
        return 0, "";
    }
}

//获得到Match数据对应实际数据的列数
func getHeadPosition(matchArr []string, headInfo string) map[string]int {
    head_m := make(map[string]int)
    for i := 3; i < len(matchArr); i++ {
        head_m[matchArr[i]] = getColumnPosition(matchArr[i], headInfo)
    }
   return head_m;
}

func getColumnPosition(match string, headInfo string) int {
    result := -1
    headInfo_arr := strings.Split(headInfo, split_column)
    for i := 0; i < len(headInfo_arr); i++ {
        if headInfo_arr[i] == match {
            return i
        }
    }
   return result;
}

// main function starts up the chaincode in the container during instantiate
func main() {
    if err := shim.Start(new(SimpleAsset)); err != nil {
            fmt.Printf("Error starting SimpleAsset chaincode: %s", err)
    }
}
