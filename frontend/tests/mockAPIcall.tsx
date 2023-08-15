import { REPLFunction } from "../src/interfaces/REPLFunction";
import { fileMap, resultMap } from "./mockAPIresp";
import Table from "../src/components/Table";
import {ReactNode} from "react";

let cur_file : string[][] | undefined = [[]];

/**
 * Handles a mock load command.
 * @param args filepath and header boolean
 */
export const mockLoad : REPLFunction = function (args: Array<string>) : Promise<string> {
    return new Promise((resolve, reject) => {
        if (args.length != 2) {
            resolve("Load_file requires a valid filepath and a boolean indicating if header exists.");
        } else {
            const file = args[0];
            const hasHeader = args[1]
            if (fileMap.has(file)) {
                const data = fileMap.get(file);
                if (data != null) {
                    cur_file = fileMap.get(file);
                    resolve("File " + file + " successfully loaded.");
                }
            } else {
                resolve("File not found.");
            }
        }
    });
}

/**
 * Handles a mock view command which returns corresponding mocked API responses.
 * @param args
 */
export const mockView : REPLFunction = function (args: Array<string>) : Promise<ReactNode> {
    return new Promise((resolve, reject) => {
        if (cur_file == undefined || cur_file[0].length == 0) {
            resolve("View requires a valid filepath to be loaded beforehand.")
        } else {
            resolve(<Table data={cur_file}/>);
        }
    });
}

/**
 * Handles a mock search command which returns corresponding mocked API responses.
 * @param args search value and col index/header
 */
export const mockSearch : REPLFunction = function (args: Array<string>) : Promise<ReactNode> {
    return new Promise((resolve, reject) => {
        if (args.length < 1) {
            resolve("Search requires a search value param, and optional col param.")
        } else {
            const col = args[1];
            const value = args[0];
            if (resultMap.has(value)) {
                const result = resultMap.get(value);
                if (result != null) {
                    resolve(<Table data={result}/>);
                }
            } else {
                resolve("Search value not found.");
            }
        }
    });
}

/**
 * Resets the loaded current CSV file
 */
export function resetFile() {
    let empty_file : string[][] | undefined = [[]];
    cur_file = empty_file;
};

export { cur_file };