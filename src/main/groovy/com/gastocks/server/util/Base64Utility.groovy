package com.gastocks.server.util

import org.springframework.stereotype.Component

@Component
class Base64Utility {

    static Object decode(String input) throws IOException, ClassNotFoundException {

        byte [] data = Base64.getDecoder().decode(input)
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))
        Object obj  = ois.readObject()
        ois.close()
        obj
    }

    static String encode(Serializable o) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ObjectOutputStream oos = new ObjectOutputStream(baos)
        oos.writeObject(o)
        oos.close()

        Base64.getEncoder().encodeToString(baos.toByteArray())
    }

}
