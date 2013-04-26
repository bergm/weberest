goog.provide('webfui.framework');
goog.require('cljs.core');
goog.require('webfui.plugin.core');
goog.require('webfui.utilities');
goog.require('webfui.dom');
goog.require('webfui.plugin.mouse');
goog.require('webfui.state_patches');
goog.require('webfui.utilities');
goog.require('webfui.state_patches');
goog.require('webfui.plugin.mouse');
goog.require('webfui.plugin.core');
goog.require('webfui.dom');
webfui.plugin.core.register_plugin.call(null,(new webfui.plugin.mouse.mouse()));
webfui.framework.state_watcher = (function state_watcher(dom,renderer,key,state,old,new$){
return cljs.core.swap_BANG_.call(null,dom,cljs.core.constantly.call(null,renderer.call(null,new$)));
});
webfui.framework.launch_app = (function launch_app(state,renderer){
var dom_31176 = cljs.core.atom.call(null,renderer.call(null,cljs.core.deref.call(null,state)));
webfui.dom.defdom.call(null,dom_31176);
cljs.core.add_watch.call(null,state,"\uFDD0'state-watcher",cljs.core.partial.call(null,webfui.framework.state_watcher,dom_31176,renderer));
webfui.framework.cur_state = state;
});
webfui.framework.add_dom_watch_helper = (function add_dom_watch_helper(id,fun){
return webfui.dom.add_dom_watch.call(null,id,(function (_,element_new){
return cljs.core.swap_BANG_.call(null,webfui.framework.cur_state,(function (state){
var diff = fun.call(null,state,element_new);
return webfui.state_patches.patch.call(null,state,diff);
}));
}));
});
webfui.framework.mouse_down_state = cljs.core.atom.call(null,null);
webfui.framework.add_mouse_watch_helper = (function add_mouse_watch_helper(id,fun,optimization){
return webfui.plugin.mouse.add_mouse_watch.call(null,id,(function (element_old,element_new,points){
cljs.core.swap_BANG_.call(null,webfui.framework.mouse_down_state,(function (old){
var or__3943__auto__ = old;
if(cljs.core.truth_(or__3943__auto__))
{return or__3943__auto__;
} else
{return cljs.core.deref.call(null,webfui.framework.cur_state);
}
}));
return cljs.core.reset_BANG_.call(null,webfui.framework.cur_state,((cljs.core._EQ_.call(null,optimization,"\uFDD0'incremental"))?(function (){var mds = cljs.core.deref.call(null,webfui.framework.mouse_down_state);
var diff = fun.call(null,mds,element_old,element_new,cljs.core.subvec.call(null,points,((0 > (cljs.core.count.call(null,points) - 2)) ? 0 : (cljs.core.count.call(null,points) - 2))));
var new_state = webfui.state_patches.patch.call(null,mds,diff);
cljs.core.reset_BANG_.call(null,webfui.framework.mouse_down_state,(cljs.core.truth_(webfui.utilities.get_attribute.call(null,element_old,"\uFDD0'active"))?new_state:null));
return new_state;
})():(function (){var mds = cljs.core.deref.call(null,webfui.framework.mouse_down_state);
var diff = fun.call(null,mds,element_old,element_new,points);
if(cljs.core.truth_(webfui.utilities.get_attribute.call(null,element_old,"\uFDD0'active")))
{} else
{cljs.core.reset_BANG_.call(null,webfui.framework.mouse_down_state,null);
}
return webfui.state_patches.patch.call(null,mds,diff);
})()));
}));
});
