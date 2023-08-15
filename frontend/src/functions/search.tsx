import { ReactNode } from "react"
import Table from "../components/Table"
import { SearchResponse } from "../interfaces/SearchResponse"

/**
 * Verifies backend API response of search API call
 * @param rjson
 */
function isSearchResponse(rjson: any): rjson is SearchResponse {   
  if(!('response_type' in rjson)) return false
  // if(!('request' in rjson)) return false
  if(!('data' in rjson)) return false
  // if(!('search' in rjson)) return false
  return true
}

/**
 * Handles the search command.
 * @param args parsed search value and col index/header
 */
export default function search(args: string[]) : Promise<ReactNode> {
  return new Promise((resolve, reject) => {
    if (args.length < 1 || args.length > 2) {
      console.log(args)
        resolve("Search expects at least 1 search term");
    } else if (args.length == 1) {
      const term : string = args[0]
        const data =
        fetch("http://localhost:3005/searchcsv?search=" + term)
            .then(response => response.json())
            .then(data => { 
                console.log(data);
                if (isSearchResponse(data)) {
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
    else {
      const term : string = args[0]
      const col : string = args[1]
      const data =
        fetch("http://localhost:3005/searchcsv?search=" + term + "&column=" + col)
            .then(response => response.json())
            .then(data => { 
                console.log(data);
                if (isSearchResponse(data)) {
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