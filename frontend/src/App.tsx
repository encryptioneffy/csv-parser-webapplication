import { useState } from "react";
import "../styles/App.css";
import Header from "./components/Header";
import HistoryBox from "./components/HistoryBox";
import InputBox from "./components/InputBox";
import { HistoryCommand } from "./interfaces/HistoryCommand";
import { Mode } from "./interfaces/mode";

/**
 * Top level div containers of the REPL app. Contains the HistoryBox and InputBox.
 * @constructor
 */


function App() {
  // The data state is an array of strings, which is passed to our components
  // You may want to make this a more complex object, but for now it's just a string
  const [history, setHistory] = useState<HistoryCommand[]>([]);
  const [mode, setMode] = useState<Mode>(Mode.brief)

  function handleKeyPress(event: KeyboardEvent) {
    // The event has more fields than just the key pressed (e.g., Alt, Ctrl, etc.)
    if (event.ctrlKey) {
      if(event.key === ";") {
        const element : HTMLElement | null = document.getElementById('repl-input')
        if(element != null) {
          console.log("Got element repl-input")
          element.focus()
        } else {
          console.log("Could not get repl-input element")
        }
      } else if(event.key === "/") {
        const element : HTMLElement | null = document.getElementById('repl-history')
        if(element!= null) {
          console.log("Got element repl-history")
          element.focus()
        } else {
          console.log("Could not find repl-history element")
        }
      }
    }
  }
  
  window.addEventListener('keydown', handleKeyPress)

  return (
    <div
        aria-label="REPL Web Application container"
        aria-description="Welcome to the REPL command-line application. Begin by clicking tab to navigate into the
        input box and enter a command."
    >
      <Header />
      <div className="repl">
        <HistoryBox history={history} mode={mode}/>
        <hr />
        <InputBox mode={mode} setMode={setMode} history={history} setHistory={setHistory}/>
      </div>
    </div>
  );
}

export default App;
