package seamcarving.exception

class ElementNotFoundException(elementName: String) : Throwable("Element wasn't found \"$elementName\"")
