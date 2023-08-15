import { HistoryCommand } from "./HistoryCommand";
import { Mode } from "./mode";

export interface InputBoxProps {
  mode: Mode;
  setMode: (mode: Mode) => void;
  history: HistoryCommand[];
  setHistory: (data: HistoryCommand[]) => void;
}