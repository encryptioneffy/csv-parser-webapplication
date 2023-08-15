import { ReactNode } from "react"

export interface HistoryCommand {
  command: string
  response: ReactNode
  className: string
}
