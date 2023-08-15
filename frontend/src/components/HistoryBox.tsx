import { HistoryCommand } from "../interfaces/HistoryCommand";
import { Mode } from "../interfaces/mode";

export const hist_accessible_name = "History log of commands and corresponding outputs";
export const command_accessible_name = "Command entered"
export const output_accessible_name = "Output of command"
interface HistoryBoxProps {
  history: HistoryCommand[];
  mode: Mode;
}

/**
 * Creates & updates REPL-History Box
 * @param props - props containing history[] & mode
 * @constructor
 */
function HistoryBox(props: HistoryBoxProps) {
  let output; 
  if(props.mode === Mode.brief) {
    output = props.history.map((item, index) => (
        <div className={item.className} key={index} aria-label={"output of command"}>{item.response}</div>
      ))
  } else {
    output = props.history.map((item, index) => (
      <div className={item.className} key={index} aria-label={"Verbose Response"}>
        <p aria-label={command_accessible_name} role={"command"} >COMMAND: {item.command}</p>
        <p aria-label={output_accessible_name} role={"output"}>OUTPUT: {item.response}</p>
      </div>
    ))
  }


  return (
    <div id="repl-history" className="repl-history" data-testid="repl-history" 
         aria-label={hist_accessible_name}>
      {output}
    </div>
  );
}

export default HistoryBox;
