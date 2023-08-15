export const header_accessible_name = "REPL title";

/**
 * Outputs header with accessible aria-label
 * @constructor
 */
function Header() {
  return <h1 aria-label={header_accessible_name}
             aria-description="Welcome to the REPL application. Begin by clicking tab to navigate into the input box
             to enter commands."
  >REPL</h1>;
}

export default Header;
