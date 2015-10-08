package hudson.plugins.project_inheritance.projects;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


import hudson.plugins.project_inheritance.projects.references.AbstractProjectReference;

/**
 * Final class for help to keep a cache of AbstractProjectReferences
 * We must implement this as a very simple approaching.
 */
public final class InheritanceHelper {
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock( true );

    private Map<String, List<AbstractProjectReference>> ALL_PARENT_REFERENCES = new HashMap<String, List<AbstractProjectReference>>();

    public static List<AbstractProjectReference> getAllParentReferences( InheritableProject project ) {
        LOCK.readLock().lock();
        try {
            List<AbstractProjectReference> references = CACHE.get( ALL_PARENT_REFERENCES_KEY );
            if ( references != null ) {
                return references;
            }
        } finally {
            LOCK.readLock().unlock();
        }
        // We didn't find the references so we must try to compute them


    }



    @Override
    public void onDeleted( Item item ) {
        this.invalidateCache();
    }


    /**
     * We invalidate the cache because it receives an event that invalidates our current tree
     *
     */
    private static void invalidateCache() {
        LOCK.writeLock().lock();
        try {
            CACHE.clear();

        } finally {
            LOCK.writeLock().unlock();
        }
    }
}