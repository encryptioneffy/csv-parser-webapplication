import "@testing-library/jest-dom";
import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import App from "../src/App";
import {addCommand, removeCommand, validCommands,
    input_box_accessible_name, button_accessible_name } from "../src/components/InputBox";
import { hist_accessible_name, output_accessible_name, command_accessible_name } from "../src/components/HistoryBox";
import { mockLoad, mockSearch, mockView, resetFile, cur_file} from "./mockAPIcall";


/** Note that calls to user.click(button) on the submit button is calling and testing
 * the handleCommand & handleSubmit functions. */


/** Set Up */
// set up: instantiating elements accessed by all tests
let history: HTMLElement;
let input: HTMLInputElement;
let button: HTMLElement;

// replace REPL functions with mock functions
function initFuncMap() {
    removeCommand("load_file");
    removeCommand("view");
    removeCommand("search");
    addCommand("mock_load", mockLoad);
    addCommand("mock_view", mockView);
    addCommand("mock_search", mockSearch);
}

// helper function for checking command
function checkCommand(cmd: string) {
    return validCommands.get(cmd) !== undefined;
}

// set up test environment
beforeEach(() => {
    render(<App />);
    initFuncMap();
    resetFile();
    history = screen.getByTestId("repl-history")
    input = screen.getByRole("textbox", { name: input_box_accessible_name})
    button = screen.getByRole("button", { name: button_accessible_name });
})


/** Unit test for registration code */
test("loads and displays header", async () => {
    expect(screen.getByText(/REPL/i)).toBeInTheDocument();
});

//testing the commands populate the map correctly
test("register command", async () => {
    expect(checkCommand("mock_load")).toBe(true);
    expect(checkCommand("mock_view")).toBe(true);
    expect(checkCommand("mock_search")).toBe(true);
    expect(checkCommand("mock_invalid")).toBe(false);
});

//testing the commands are removed from the map correctly
test("unregister command", async () => {
    expect(removeCommand("mock_load")).toBe(true);
    expect(removeCommand("mock_load")).toBe(false);
    expect(checkCommand("mock_load")).toBe(false);
    expect(removeCommand("mock_invalid")).toBe(false);
});

test("register new command", async () => {
    // register a new command
    addCommand("mock_load", mockLoad);
    expect(checkCommand("mock_load")).toBe(true);
});


/** Mock tests utilizing accessibility-queries */
test("mode command", async () => {
    //check the mode is initially set to brief
    let user = userEvent.setup();
    await user.type(input, "mode");
    await user.click(button);
    expect(
        await screen.findByText("OUTPUT: Mode set from brief to verbose")).toBeInTheDocument();
    const command = screen.getByRole("command", {name: command_accessible_name});
    expect(command).toBeVisible();

    //check the mode can be set to verbose
    await user.type(input, "mode");
    await user.click(button);
    expect(
        await screen.findByText("Mode set from verbose to brief")
    ).toBeInTheDocument();
    expect(command).not.toBeVisible();


    //setting the mode back to brief
    await user.type(input, "mode");
    await user.click(button);
    const outputs = screen.getAllByRole("output", { name: output_accessible_name });
    expect(outputs).toHaveLength(3);
});


test("load_file no filepath param", async () => {
    //command with no filepath
    let user = userEvent.setup();
    await user.type(input, "mock_load");
    await expect(input.value).toBe("mock_load");
    await user.click(button);
    expect(
        await screen.findByText(
            "Load_file requires a valid filepath and a boolean indicating if header exists."
        )
    ).toBeInTheDocument();
});

test("load_file invalid filepath param", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load doesnotexist true");
    await expect(input.value).toBe("mock_load doesnotexist true");
    await user.click(button);
    expect(
        await screen.findByText(
            "File not found."
        )
    ).toBeInTheDocument();
});

test("load_file successful response", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockSimple false");
    await expect(input.value).toBe("mock_load mockSimple false");
    await user.click(button);
    expect(await screen.findByText("File mockSimple successfully loaded.")).toBeInTheDocument();
});


test("view without loading csv", async () => {
    let user = userEvent.setup();
    expect(cur_file).toHaveLength(1);
    await user.type(input, "mock_view");
    await expect(input.value).toBe("mock_view");
    await user.click(button);
    expect(
        await screen.findByText("View requires a valid filepath to be loaded beforehand.")
    ).toBeInTheDocument();
});

test("view success, no header",async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockSimple false");
    await expect(input.value).toBe("mock_load mockSimple false");
    await user.click(button);
    await user.type(input, "mock_view");
    await expect(input.value).toBe("mock_view");
    await user.click(button);
    const table = document.getElementsByClassName("tableCSVRow").item(0);
    expect(table).not.toBeNull();
    if (table !== null) {
    expect(table.innerHTML).toEqual(
        "<td class=\"tableCSVElement\" id=\"row-0-col-0\">The</td>" +
        "<td class=\"tableCSVElement\" id=\"row-0-col-1\">song</td>" +
        "<td class=\"tableCSVElement\" id=\"row-0-col-2\">remains</td>" +
        "<td class=\"tableCSVElement\" id=\"row-0-col-3\">the</td>" +
        "<td class=\"tableCSVElement\" id=\"row-0-col-4\">same.</td>");
    }
});

test("view success, larger csv with header", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockStars true");
    await expect(input.value).toBe("mock_load mockStars true");
    await user.click(button);
    await user.type(input, "mock_view");
    await expect(input.value).toBe("mock_view");
    await user.click(button);
    const firstRow = document.getElementById("row-0");
    expect(firstRow).not.toBeNull();
    if (firstRow !== null) {
        expect(firstRow.innerHTML).toEqual(
            "<td class=\"tableCSVElement\" id=\"row-0-col-0\">StarID</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-1\">ProperName</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-2\">X</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-3\">Y</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-4\">Z</td>");
    }
    const lastRow = document.getElementById("row-10");
    expect(lastRow).not.toBeNull();
    expect(document.getElementsByClassName("tableCSVRow").length).toEqual(11);
});

test("search no params/bad params", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockAnimals true");
    await expect(input.value).toBe("mock_load mockAnimals true");
    await user.click(button);

    await user.type(input, "mock_search");
    await expect(input.value).toBe("mock_search");
    await user.click(button);
    expect(await screen.findByText(
        "Search requires a search value param, and optional col param.")).toBeInTheDocument();
});

test("search empty CSV", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockEmpty true");
    await expect(input.value).toBe("mock_load mockEmpty true");
    await user.click(button);

    await user.type(input, "mock_search foo banana");
    await expect(input.value).toBe("mock_search foo banana");
    await user.click(button);
    expect(await screen.findByText("Search value not found.")).toBeInTheDocument();
});


test("search term not in CSV file", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockAnimals true");
    await expect(input.value).toBe("mock_load mockAnimals true");
    await user.click(button);

    await user.type(input, "mock_search foo banana");
    await expect(input.value).toBe("mock_search foo banana");
    await user.click(button);
    expect(await screen.findByText("Search value not found.")).toBeInTheDocument();
});

test("search success, no header", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockAnimals true");
    await expect(input.value).toBe("mock_load mockAnimals true");
    await user.click(button);
    await user.type(input, "mock_search orange");
    await expect(input.value).toBe("mock_search orange");
    await user.click(button);
    const table = document.getElementById("row-0");
    expect(table).not.toBeNull();
    if (table !== null) {
        expect(table.innerHTML).toEqual(
            "<td class=\"tableCSVElement\" id=\"row-0-col-0\">cat</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-1\">orange</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-2\">4</td>");
    }
});

test("search success, with header & col index", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockStars true");
    await expect(input.value).toBe("mock_load mockStars true");
    await user.click(button);
    await user.type(input, "mock_search -0.47175 2");
    await expect(input.value).toBe("mock_search -0.47175 2");
    await user.click(button);
    // expect(await screen.getByRole("helper", {name: ""})).toBeInTheDocument();

    const table = document.getElementById("row-0");
    expect(table).not.toBeNull();

    const entries = document.getElementsByClassName("tableCSVElement");
    expect(entries.length).toEqual(5);
    expect(entries[1].innerHTML).toEqual(
        "Proxima Centauri");
});


test("search success, with header & col header name", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockStars true");
    await expect(input.value).toBe("mock_load mockStars true");
    await user.click(button);
    await user.type(input, "mock_search Sol ProperName");
    await expect(input.value).toBe("mock_search Sol ProperName");
    await user.click(button);

    const table = document.getElementById("row-0");
    expect(table).not.toBeNull();

    const entries = document.getElementsByClassName("tableCSVElement");
    expect(entries.length).toBe(5);
    expect(entries[1].innerHTML).toEqual(
        "Sol");
});


test("search in file but wrong col/header", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockStar true");
    await expect(input.value).toBe("mock_load mockStar true");
    await user.click(button);
    await user.type(input, "mock_search 0 Z");
    await expect(input.value).toBe("mock_search 0 Z");
    await user.click(button);

    expect(await screen.findByText("Search value not found.")).toBeInTheDocument();
});

test("search with invalid header", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockStar true");
    await expect(input.value).toBe("mock_load mockStar true");
    await user.click(button);
    await user.type(input, "mock_search 0 gibby");
    await expect(input.value).toBe("mock_search 0 gibby");
    await user.click(button);

    expect(await screen.findByText("Search value not found.")).toBeInTheDocument();
});

test("search with column out of founds", async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockStar true");
    await expect(input.value).toBe("mock_load mockStar true");
    await user.click(button);
    await user.type(input, "mock_search 0 100000000000");
    await expect(input.value).toBe("mock_search 0 100000000000");
    await user.click(button);

    expect(await screen.findByText("Search value not found.")).toBeInTheDocument();
});

test("test multiple instances of state-modifying command. (load, search, view x2) ",
    async () => {
    let user = userEvent.setup();
    await user.type(input, "mock_load mockStars false");
    await expect(input.value).toBe("mock_load mockStars false");
    await user.click(button);

    await user.type(input, "mock_view");
    await expect(input.value).toBe("mock_view");
    await user.click(button);

    const firstRow = document.getElementById("row-0");
    expect(firstRow).not.toBeNull();
    if (firstRow !== null) {
        expect(firstRow.innerHTML).toEqual(
            "<td class=\"tableCSVElement\" id=\"row-0-col-0\">StarID</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-1\">ProperName</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-2\">X</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-3\">Y</td>" +
            "<td class=\"tableCSVElement\" id=\"row-0-col-4\">Z</td>");
    }

    await user.type(input, "mock_search Sol ProperName");
    await expect(input.value).toBe("mock_search Sol ProperName");
    await user.click(button);

    // change the mode, check that commands are visible
    await user.type(input, "mode");
    await user.click(button);
    expect(
        await screen.findByText("OUTPUT: Mode set from brief to verbose")).toBeInTheDocument();
    const command = screen.getAllByRole("command", {name: "Command entered"});
    const firstCommand = command[0];
    expect(firstCommand).toBeVisible()

    //load a new file
    await user.type(input, "mock_load mockSimple false");
    await expect(input.value).toBe("mock_load mockSimple false");
    await user.click(button);
    expect(await screen.findByText("OUTPUT: File mockSimple successfully loaded.")).toBeInTheDocument();

    //check that search reflects new file loaded
    await user.type(input, "mock_search remains");
    await expect(input.value).toBe("mock_search remains");
    await user.click(button);
    const table = document.getElementsByClassName("tableCSV");
    expect(table.length).toBe(3);

    //check that view reflects new file loaded
    await user.type(input, "mock_view");
    await expect(input.value).toBe("mock_view");
    await user.click(button);

    const rows = document.getElementsByClassName("tableCSVElement");
    expect(rows.length).toBe(70);
});








