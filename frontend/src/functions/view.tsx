import { ReactNode } from "react"
import Table from "../components/Table"
import { ViewResponse } from "../interfaces/ViewResponse"

/**
 * Verifies backend API response of view API call
 * @param rjson
 */
function isViewResponse(rjson: any): rjson is ViewResponse {   
  if(!('response_type' in rjson)) return false
  if(!('request' in rjson)) return false
  if(!('data' in rjson)) return false
  return true
}

/**
 * Handles the view command
 * @param args - parsed command following "view" should be empty
 */
export default function view(args: string[]) : Promise<ReactNode> {
  return new Promise((resolve, reject) => {
    if (args.length !== 0) {
      console.log(args)
        resolve("View expects 0 parameters");
    } else {
        const data =
        fetch("http://localhost:3005/viewcsv")
            .then(response => response.json())
            .then(data => { 
                console.log(data);
                if (isViewResponse(data)) {
                  if (data.response_type !== "success") {
                      resolve(data.response_type);
                  } else {
                      resolve(<Table data={data.data}/>);
                  }
              }
            })
            .catch(e => {
                resolve(e.message)
            });
    }
})
}