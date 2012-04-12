#Test runner for use with PhantomJS
page = new WebPage()
page.onConsoleMessage = (msg) ->
  phantom.exit() if msg == "__exit__"
  console.log msg

page.open "public/index.html"

