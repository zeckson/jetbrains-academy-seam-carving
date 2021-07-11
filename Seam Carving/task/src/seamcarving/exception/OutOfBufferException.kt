package seamcarving.exception

class OutOfBufferException(size: Int, x: Int, y: Int) :
        RuntimeException("$x, $y is out from buffer size: $size")
