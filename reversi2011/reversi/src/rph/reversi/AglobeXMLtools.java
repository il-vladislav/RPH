package rph.reversi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <p>Title: A-Globe XML Tools</p>
 * <p>Description: This class contains static utility methods for XML marshaling and unmarshalling.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Gerstner Laboratory</p>
 * @author David Sislak
 * @version $Revision: 1.3 $ $Date: 2010/11/25 15:43:52 $
 *
 */
public final class AglobeXMLtools {
    /**
     * XML extension
     */
    public static final String XMLEXT = ".xml";

    /**
     * Cached jaxb contexts
     */
    private static final HashMap<ClassLoader, HashMap<String,JAXBContext>> jaxbContexts = new HashMap<ClassLoader, HashMap<String,JAXBContext>>();

    /**
     * Keeps contexts marshallers
     */
    private static final HashMap<JAXBContext, Marshaller> jaxbMarshallers = new HashMap<JAXBContext,Marshaller>();

    /**
     * Keeps contexts unmarshallers
     */
    private static final HashMap<JAXBContext, Unmarshaller> jaxbUnmarshallers = new HashMap<JAXBContext,Unmarshaller>();

    /**
     * JAXB sync object
     */
    private static final ReentrantLock jaxbSync = new ReentrantLock();

    /**
     * Singleton class
     */
    private AglobeXMLtools() {

    }

    /**
     * Get jaxb context for the specified jaxb object
     *
     * @param object Object - Jaxb marshallable root element
     * @return JAXBContext
     * @throws JAXBException
     */
    private final static JAXBContext getJAXBContext(final Object object) throws JAXBException {
        String contextPath;
        ClassLoader classLoader;
        JAXBContext retVal;
        if (object instanceof Class) {
            Class<?> c = (Class<?>) object;
            contextPath = c.getPackage().getName();
            classLoader = c.getClassLoader();
        } else {
            contextPath = object.getClass().getPackage().getName();
            classLoader = object.getClass().getClassLoader();
        }
        // try to read JAXBContext from the previous one first
        HashMap<String,JAXBContext> precachedContexts = jaxbContexts.get(classLoader);
        if (precachedContexts == null) {
            precachedContexts = new HashMap<String,JAXBContext>();
            jaxbContexts.put(classLoader, precachedContexts);
        }
        retVal = precachedContexts.get(contextPath);
        if (retVal != null) {
            return retVal;
        }
        // create new context
        retVal = JAXBContext.newInstance(contextPath, classLoader);
        precachedContexts.put(contextPath, retVal);
        return retVal;
    }

    /**
     * Get jaxb marshaller for specified jaxb object
     *
     * @param object Object
     * @return Marshaller
     * @throws JAXBException
     */
    private final static Marshaller getJAXBMarshaller(final Object object) throws JAXBException {
        final JAXBContext jc = getJAXBContext(object);
        Marshaller retVal = jaxbMarshallers.get(jc);
        if (retVal != null) {
            return retVal;
        }
        retVal = jc.createMarshaller();
        retVal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        jaxbMarshallers.put(jc, retVal);
        return retVal;
    }

    /**
     * Get jaxb unmarshaller for specified jaxb object
     * @param object Object
     * @return Unmarshaller
     * @throws JAXBException
     */
    private final static Unmarshaller getJAXBUnmarshaller(final Object object) throws JAXBException {
        final JAXBContext jc = getJAXBContext(object);
        Unmarshaller retVal = jaxbUnmarshallers.get(jc);
        if (retVal != null) {
            return retVal;
        }
        retVal = jc.createUnmarshaller();
        jaxbUnmarshallers.put(jc, retVal);
        return retVal;
    }

    /**
     * Marshall JAXB object to the specified output stream
     *
     * @param object Object
     * @param outputStream OutputStream
     * @throws JAXBException
     */
    public final static void marshallJAXBObject(final Object object, final OutputStream outputStream) throws JAXBException {
        jaxbSync.lock();
        try {
            final Marshaller marshaller = getJAXBMarshaller(object);
            marshaller.marshal(object, outputStream);
        } finally {
            jaxbSync.unlock();
        }
    }

    /**
     * Unmarshall JAXB object form the specified input stream
     *
     * @param defClass Class - defining class which is expected to be read
     * @param inputStream InputStream
     * @return Object
     * @throws JAXBException
     */
    public final static Object unmarshallJAXBObject(final Class<?> defClass, final InputStream inputStream) throws JAXBException {
        return unmarshallJAXBObject(defClass, inputStream, null, false);
    }

    /**
     * Unmarshall JAXB object form the specified input stream. Use this method if the unmarshall is
     * called from other running unmarshaller, e.g. from ObjectAdapter
     *
     * @param defClass Class
     * @param inputStream InputStream
     * @param doNotShareUnmarshaller boolean
     * @return Object
     * @throws JAXBException
     */
    public static Object unmarshallJAXBObject(Class<?> defClass, InputStream inputStream, boolean doNotShareUnmarshaller) throws
            JAXBException {
        return unmarshallJAXBObject(defClass, inputStream, null, doNotShareUnmarshaller);
    }

    /**
     * Unmarshall JAXB object form the specified input stream
     *
     * @param defClass Class - defining class which is expected to be read
     * @param inputStream InputStream
     * @param adapters XmlAdapter[] - array of XML adapters which should be used during unmarshalling
     * @param doNotShareUnmarshaller boolean
     * @return Object
     * @throws JAXBException
     */
    public final static Object unmarshallJAXBObject(final Class<?> defClass, final InputStream inputStream, final XmlAdapter<?,?>[] adapters, boolean doNotShareUnmarshaller) throws JAXBException {
        jaxbSync.lock();
        try {
            Unmarshaller unmarshaller;
            if (!doNotShareUnmarshaller) {
                // try get unmarshaller from shared set
                unmarshaller = getJAXBUnmarshaller(defClass);
            } else {
                // create new unique unmarshaller
                final JAXBContext jc = getJAXBContext(defClass);
                unmarshaller = jc.createUnmarshaller();
            }
            if (adapters != null) {
                for (final XmlAdapter<?, ?> elem : adapters) {
                    unmarshaller.setAdapter(elem);
                }
            }
            return unmarshaller.unmarshal(inputStream);
        } finally {
            jaxbSync.unlock();
        }
    }

    /**
     * Unmarshall JAXB object form the specified input stream. Use this method if the unmarshall is
     * called from other running unmarshaller, e.g. from ObjectAdapter
     *
     * @param defClass Class
     * @param inputStream InputStream
     * @param adapters XmlAdapter[]
     * @return Object
     * @throws JAXBException
     */
    public final static Object unmarshallJAXBObject(final Class<?> defClass, final InputStream inputStream, final XmlAdapter<?,?>[] adapters) throws JAXBException {
        return unmarshallJAXBObject(defClass, inputStream, adapters, false);
    }


    /**
     * Print object to the string representation
     * @param o Object
     * @return String
     */
    public final static String printObject(final Object o) {
        if (o instanceof byte[]) {
            final byte[] b = (byte[]) o;
            final StringBuilder sb = new StringBuilder("byte[]{");
            boolean sc = false;
            for (int i = 0; i < b.length; i++) {
                if (sc) {
                    sb.append(";");
                }
                sb.append(b[i]);
                sc = true;
            }
            sb.append("}");
            return sb.toString();
        } else {
            return o.toString();
        }
    }

}
