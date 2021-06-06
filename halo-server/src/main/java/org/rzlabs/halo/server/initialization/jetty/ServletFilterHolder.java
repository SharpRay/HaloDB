package org.rzlabs.halo.server.initialization.jetty;

import org.rzlabs.halo.guice.annotations.ExtensionPoint;

import javax.annotation.Nullable;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;
import java.util.Map;

/**
 * A ServletFilterHolder is a class that holds all of the information required to attach a Filter to a Servlet.
 *
 * This largely exists just to make it possible to add Filters via Guice and shouldn't really exist anywhere
 * that is not initialization code.
 *
 * Note that some of the HaloDB services (omniscient for example) use async servlets and your filter
 * implementation should be able to handle those requests properly.
 */
@ExtensionPoint
public interface ServletFilterHolder {

    /**
     * Get the Filter object that should be added to the servlet.
     *
     * This method is considered "mutually exclusive" from the getFilterClass method.
     * That is, one of them should return null and the other should return an actual value.
     *
     * @return The Filter object to be added to the servlet
     */
    Filter getFilter();

    /**
     * Get the class of the Filter object that should be added to the servlet.
     *
     * This method is considered "mutually exclusive" from the getFilter method.
     * That is, one of them should return null and the other should return an actual value.
     *
     * @return The class of the Filter object to be added to the servlet
     */
    Class<? extends Filter> getFilterClass();

    /**
     * Get Filter initialization parameters.
     *
     * @return a map containing all the Filter initialization parameters
     */
    Map<String, String> getInitParameters();

    /**
     * The paths that this Filter should apply to
     *
     * @return the paths that this Filter should apply to
     */
    String[] getPaths();

    /**
     * The dispatcher type that this Filter should apply to
     *
     * @return the enumeration of DispatcherTypes that this Filter should apply to
     */
    @Nullable
    EnumSet<DispatcherType> getDispatcherType();
}
