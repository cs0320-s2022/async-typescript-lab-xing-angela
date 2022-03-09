// TODO: select the list element where the suggestions should go, and all three dropdown elements
//  HINT: look at the HTML
const suggestionsLst = document.getElementById("suggestions") as HTMLUListElement

// Here, when the value of sun is changed, we will call the method postAndUpdate.
// TODO: Do the same for moon and rising
const moon = document.getElementById("moon") as HTMLInputElement
const sun = document.getElementById("sun") as HTMLInputElement
const rising = document.getElementById("rising") as HTMLInputElement

moon.addEventListener("change", () => postAndUpdate());
sun.addEventListener("change", () => postAndUpdate());
rising.addEventListener("change", () => postAndUpdate());

// TODO: Define a type for the request data object here.
type MatchesRequestData = {sun: string, moon: string, rising: string}

// TODO: Define a type for the response data object here.
type Matches = {suggestionsLst: string[]}

function postAndUpdate(): void {
  // TODO: empty the suggestionList (you want new suggestions each time someone types something new)
  //  HINT: use .innerHTML
  suggestionsLst.innerHTML = "";

  // TODO: add a type annotation to make this of type MatchesRequestData
  const postParameters: MatchesRequestData = {
    // TODO: get the text inside the input box
    //  HINT: use sun.value to get the value of the sun field, for example
    moon : moon.value,
    sun : sun.value,
    rising : rising.value
  };

  //console.log(postParameters)

  // TODO: make a POST request using fetch to the URL to handle this request you set in your Main.java
  //  HINT: check out the POST REQUESTS section of the lab and of the front-end guide.
  //  Make sure you add "Access-Control-Allow-Origin":"*" to your headers.
  //  Remember to add a type annotation for the response data using the Matches type you defined above!
  fetch ("http://localhost:4567/results", {
    method: "post",
    body: JSON.stringify(postParameters),
    headers: {'Access-Control-Allow-Origin':'*',
      'Content-Type': 'application/json; charset=UTF-8',
    }
  })

      // TODO: Call and fill in the updateSuggestions method in one of the .then statements in the Promise
      //  Parse the JSON in the response object
      //  HINT: remember to get the specific field in the JSON you want to use
      .then((response:Response) => response.json())
      .then((matches: Matches) => updateSuggestions(matches['suggestionsLst']))
      .then((error: any) => console.log("Error:", error))
}

function updateSuggestions(matches: string[]): void {
  // TODO: for each element in the set of matches, append it to the suggestionList
  //  HINT: use innerHTML += to append to the suggestions list
  //  NOTE: you should use <li> (list item) tags to wrap each element. When you do so,
  //  make sure to add the attribute 'tabindex="0"' (for example: <li tabindex="0">{your element}</li>).
  //  This makes each element selectable via screen reader.
  for (let i = 0; i < matches.length; i++) {
    suggestionsLst.innerHTML += `<li tabindex="0">${matches[i]}</li>`
  }
}

// TODO: create an event listener to the document (document.addEventListener) that detects "keyup".
//  When a certain key of your choice is clicked, reset the values of sun, moon, and rising to your own
//  values for the sun, moon, and rising using updateValues. Then call postAndUpdate().
//  HINT: the listener callback function should be asynchronous and wait until the values are
//  updated before calling postAndUpdate().

document.addEventListener("keyup", keyListener);

async function keyListener(key: any) {
  // I learned the switch statement syntax for this from the link above
  switch (key.code) {
    case "KeyA":
      await updateValues("Scorpio", "Libra", "Leo");
      postAndUpdate();
      break;
  }
}

async function updateValues(sunval: string, moonval: string, risingval: string): Promise<void>{
  // This line asynchronously waits 1 second before updating the values.
  // It's unnecessary here, but it simulates asynchronous behavior you often have to account for.
  await new Promise(resolve => setTimeout(resolve, 1000));

  sun.value = sunval;
  moon.value = moonval;
  rising.value = risingval;
}
