import { ReactNode } from "react";
import { TableProps } from "../interfaces/TableProps";

/**
 * Aria-Label Constant for data table.
 */
export const data_table_accessible_name = "Resulting Table for view/search."

/**
 * Turns a 2D string array into a HTML table
 * @param props - TableProps containing the 2D array
 * @constructor
 */
export default function Table(props: TableProps) {
  return (
    <table className="tableCSV" data-testid={"table"} aria-label={data_table_accessible_name}
           aria-description="This is a data table in the history log.">
        <tbody>
                {props.data.map((row, rowIndex) => {
                    return (
                    <tr className="tableCSVRow" id={"row-" + rowIndex.toString()}>
                        {row.map((item, colIndex) => {
                            return (
                                <td className="tableCSVElement" id={"row-" + rowIndex.toString() + "-col-" + colIndex.toString()}>
                                    {item}
                                </td>
                            );
                        })}
                    </tr>
                    );
                })}
            </tbody>
    </table>
  )
}