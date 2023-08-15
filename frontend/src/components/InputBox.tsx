import { ReactNode, useState } from "react";
import load from "../functions/load";
import search from "../functions/search";
import view from "../functions/view";
import { InputBoxProps } from "../interfaces/InputBoxProps";
import { Mode } from "../interfaces/mode";
import { REPLFunction } from "../interfaces/REPLFunction";
export { addCommand, removeCommand, validCommands };

/**
 * Accessible Label Constants that are used for tests. This allows for the aria-labels to be updated,
 * without needing to update the tests that call on accessibility metadata.
 * */
export const input_box_accessible_name = "Command input box";
export const button_accessible_name = "Submit button";

/** Hashmap storing registered commands for User Story 6. */
const validCommands : Map<string, REPLFunction> = new Map()

/**
 * Function for registering new commands to validCommands.
 * @param command name of command
 * @param func corresponding function
 */
function addCommand(command: string, func: REPLFunction) {
  return validCommands.set(command, func)
}

/**
 * Function for removing commands from register.
 * @param command key name of existing command in register
 */
function removeCommand(command : string) {
    return validCommands.delete(command);
}

/**
 * Register the commands required by all other user stories.
 */
function initCommands() {
  addCommand("load_file", load)
  addCommand("view", view)
  addCommand("search", search)
}


/**
 * Processes the command in the input textbox
 * @param props - InputBoxProps containing mode, setMode, history, and setHistory
 * @constructor
 */
export default function InputBox(props: InputBoxProps) {
  const [textBox, setTextBox] = useState("");
  initCommands()

  /**
   * Handles the submit button being clicked or the enter key being pressed!
   * You may want to make this function more sophisticated to add real
   * command logic, but for now it just adds the text to the history box.
   */
  function handleSubmit(response: Promise<ReactNode>, className: string) {
    // TODO: Add the text from the textbox to the history
    response
    .then(result => {
      props.setHistory([...props.history, {command: textBox, className: className, response: result}]);
      console.log(result)
    }
      
    )
      // Clears the textbox
    setTextBox("")
  }

    /**
     * Function for handling mode, load, view, and search commands
     * @param args parsed user input from input text box
     */
 async function handleCommand(args: string[]) {
  // when we add the rest of the commands, pass in the params array
  if(args.length > 0) {
    //command: mode, load, view, search
    let cmd : string = args[0]
    let params : string[] = args.slice(1)
    let output : string = "";
    if(cmd === "mode") {
      if(props.mode === Mode.brief) {
        props.setMode(Mode.verbose)
        output = "Mode set from brief to verbose"
      } else {
        props.setMode(Mode.brief)
        output = "Mode set from verbose to brief"
      }
      handleSubmit(Promise.resolve(output), "command_mode")
    } else if(validCommands.has(cmd.toLowerCase())) {
      const func = validCommands.get(cmd)
      if(func !== undefined) {
        handleSubmit(func(params), "command_" + cmd)
      }
    } else {
      handleSubmit(Promise.resolve("Not a valid command"), "unrecognized-command")

    }
  }
 }

  return (
    <div className="repl-input">
      {/* Make this input box sync with the state variable */}
      <input
        aria-label={input_box_accessible_name}
        aria-description={"Please input a command here and then hit the enter key or press the submit button"}
        aria-live={"off"}
        role={"textbox"}
        type="text"
        placeholder={"Enter command here."}
        className="repl-command-box" 
        value={textBox}
        id="repl-input" 
        onChange={(e => setTextBox(e.target.value))}
        onKeyUp={(e) => {
          if (e.key === "Enter") {
            // handleSubmit()
            handleCommand(textBox.split(" "))
          }
        }}/>
      {/* Make this button call handleSubmit when clicked */}
      <button className="repl-button" onClick={() => handleCommand(textBox.split(" "))}
              aria-label={button_accessible_name}
              aria-description="use this button or the enter key to input a command into the program"
              role={"button"}
      >Submit</button>
    </div>
  );
}
