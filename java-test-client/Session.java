/**
 * Created by timothy on 2/22/17.
 */

public interface Session {

    /**
     * Gets the current session uuid, used to identify this session in network requests.
     * Should be kept private: after attaching this session with a user, the session id is enough to
     * send requests as the user.
     * @return
     */
    String getSessionId();

    /**
     * Returns whether this Session object believes itself to be attached with a user.
     * @return whether this Session object believes itself to be attached with a user.
     */
    boolean isLoggedIn();

    /**
     * Returns the current User object associated with this Session, or null if there is none.
     * @return the current User object associated with this Session, or null if there is none.
     */
    User getLoggedInUser();

    /**
     * Add an AttachListener to the session object, so that when we get a response from login, we
     * can react to it.
     * @param listener the AttachListener object to add
     * @return the id of this listener for this session (for later removal)
     */
    int addAttachListener(AttachListener listener);

    /**
     * Add a DetachListener to the session object, so that when we get a response from a logout
     * (either forceful or voluntary), we can react to it.
     * @param listener the AttachListener object to add
     * @return the id of this listener for this session (for later removal)
     */
    int addDetachListener(DetachListener listener);

    /**
     * Add an UpdateListener to the session object, so that when we get a response from initial
     * update, we can react to it.
     * @param listener the UpdateListener object to add
     * @return the id of this listener for this session (for later removal)
     */
    int addUpdateListener(UpdateListener listener);
    
    /**
     * Removes an AttachListener from the session object
     * @param listenerId the id of the added listener
     * @return whether a listener with this id was found and removed
     */
    boolean removeListener(int listenerId);

    /**
     * Attempts to log into the server, attaching this session with a user.
     * When a response is returned, all AttachListeners added to the session will be notified.
     * @param username the user's username
     * @param password the user's password
     */
    void login(String username, String password);

    /**
     * Attempts to create an account on the server. If successful, this will attach this session
     * with the newly-created user.
     * When a response is returned, all AttachListeners added to the session will be notified.
     * @param username the new user's username
     * @param password the new user's password
     */
    void register(String username, String password, String name);

    /**
     * Sends a request to the server to invalidate the session attachment between the session and
     * its current user.
     * When a response is returned, all DetachListeners added to the session will be notified.
     */
    void logout();

    interface AttachListener {
        /**
         * Will be called when a successful login-attach response is received from the server.
         * Listeners are called in order of being added.
         * No parameters or event data is associated; all the relevant information will be reflected
         * in changes to the Session object.
         * @return whether the event should continue to propagate to the following listeners
         */
        boolean onAttachSuccess();

        /**
         * Will be called when a failed login-attach response is received from the server or no
         * response is received from the server.
         * Listeners are called in order of being added.
         * @param error A string containing information about the error
         * @return whether the event should continue to propagate to the following listeners
         */
        boolean onAttachFailure(String error);
    }

    interface DetachListener {
        /**
         * Will be called when a successful detach response is received from the server. This might
         * happen because of a logout() call from the client OR because the server has invalidated
         * the session (for example, because another session was attached with this user).
         * Listeners are called in order of being added.
         * No parameters or event data is associated; all the relevant information will be reflected
         * in changes to the Session object.
         * @return whether the event should continue to propagate to the following listeners
         */
        boolean onDetachSuccess();

        /**
         * Will be called when a failed detach response is received from the server or no
         * response is received from the server.
         * @param error A string containing information about the error
         * @return whether the event should continue to propagate to the following listeners
         */
        boolean onDetachFailure(String error);
    }

    interface UpdateListener {
        /**
         * Will be called when a successful update response is received from the server.
         * Listeners are called in order of being added.
         * No parameters or event data is associated; all the relevant information will be reflected
         * in changes to the Session object.
         * @return whether the event should continue to propagate to the following listeners
         */
        boolean onUpdate();        
    }
    
}