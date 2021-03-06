/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.rainbow.core.entities.Trackable;
import org.rainbow.shopping.cart.ui.web.bean.SessionBean;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class TrackableController<TEntity extends Trackable<?>, TKey extends Serializable, TSearchOptions> extends Controller<TEntity,TKey,TSearchOptions> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6917842044825541623L;
	private List<Boolean> auditColumnsStates;

    public TrackableController() {
    }

    public TrackableController(Class<?> modelClass) {
        super(modelClass);
    }

    @PostConstruct
    public void init() {
        auditColumnsStates = new ArrayList<>();
    }

    protected String getUserName() {
        return (String) SessionBean.getSession().getAttribute("username");
    }

    @Override
    public void create() throws Exception {
        TEntity current = this.getCurrent();
        String username = getUserName();
        current.setCreator(username);
        current.setUpdater(username);
        super.create();
    }

    @Override
    public void edit() throws Exception {
        TEntity current = this.getCurrent();
        String username = getUserName();
        current.setUpdater(username);
        super.edit();
    }

    @Override
    public void delete() throws Exception {
        TEntity current = this.getCurrent();
        String username = getUserName();
        current.setUpdater(username);
        super.delete();
    }

    public List<Boolean> getAuditColumnsStates() {
        return auditColumnsStates;
    }

    public void onToggle(ToggleEvent e) {
        Integer index = (Integer) e.getData();
        int size = auditColumnsStates.size();
        if (auditColumnsStates.size() <= index) {
            for (int i = 0; i < index - size + 1; i++) {
                auditColumnsStates.add(false);
            }
        }
        auditColumnsStates.set(index, e.getVisibility() == Visibility.VISIBLE);
    }
}
