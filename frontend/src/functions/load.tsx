import { ReactNode } from "react"
import { LoadResponse } from "../interfaces/LoadResponse"

/**
 * Verifies backend API response of load API call
 * @param rjson
 */
function isLoadResponse(rjson: any): rjson is LoadResponse {   
  if(!('response_type' in rjson)) return false
  if(!('request' in rjson)) return false
  if(!('filepath' in rjson)) return false
  if(!('headers' in rjson)) return false
  return true
}

/**
 * Handles the load command.
 * @param args parsed filepath & hboolean header
 */
export default function load(args: string[]) : Promise<ReactNode> {
  return new Promise((resolve, reject) => {
    if (args.length !== 2) {
      console.log(args)
        resolve("Load expects 2 parameters: filepath and boolean for headers");
    } else {
        const filepath = args[0];
        const headers = args[1]
        fetch("http://localhost:3005/loadcsv?filepath=" + filepath + "&headers=" + headers)
            .then(response => response.json())
            .then(data => { 
                console.log(data);
                if (isLoadResponse(data)) {
                  if(data.response_type !== "success") {
                    resolve(data.response_type)
                  } else {
                    resolve("File " + data.filepath + " successfully loaded!")
                  }
                }
            })
            .catch(e => {
                resolve(e.message)
            });
    }
})
}