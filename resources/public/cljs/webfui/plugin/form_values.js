goog.provide('webfui.plugin.form_values');
goog.require('cljs.core');
goog.require('webfui.plugin.core');
goog.require('webfui.dom_manipulation');
goog.require('webfui.dom_manipulation');
goog.require('webfui.plugin.core');
webfui.plugin.form_values.input = (function input(dom_watchers,parsed_html,event){
var target = event.target;
var vec__31178 = webfui.dom_manipulation.resolve_target.call(null,cljs.core.deref.call(null,parsed_html),target);
var tagname = cljs.core.nth.call(null,vec__31178,0,null);
var attributes = cljs.core.nth.call(null,vec__31178,1,null);
var element = vec__31178;
var event__$1 = cljs.core.deref.call(null,dom_watchers).call(null,cljs.core.keyword.call(null,(new cljs.core.Keyword("\uFDD0'watch")).call(null,attributes)));
if(cljs.core.truth_((function (){var and__3941__auto__ = event__$1;
if(cljs.core.truth_(and__3941__auto__))
{return cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.fromArray(["\uFDD0'textarea","\uFDD0'input"]),tagname);
} else
{return and__3941__auto__;
}
})()))
{var value = target.value;
var new_element = cljs.core.update_in.call(null,element,cljs.core.PersistentVector.fromArray([1,"\uFDD0'value"], true),(function (old){
target.value = old;
return value;
}));
return event__$1.call(null,element,new_element);
} else
{return null;
}
});
goog.provide('webfui.plugin.form_values.form_values');

/**
* @constructor
*/
webfui.plugin.form_values.form_values = (function (){
})
webfui.plugin.form_values.form_values.cljs$lang$type = true;
webfui.plugin.form_values.form_values.cljs$lang$ctorPrSeq = (function (this__12534__auto__){
return cljs.core.list.call(null,"webfui.plugin.form-values/form-values");
});
webfui.plugin.form_values.form_values.cljs$lang$ctorPrWriter = (function (this__12534__auto__,writer__12535__auto__,opt__12536__auto__){
return cljs.core._write.call(null,writer__12535__auto__,"webfui.plugin.form-values/form-values");
});
webfui.plugin.form_values.form_values.prototype.webfui$plugin$core$Plugin$ = true;
webfui.plugin.form_values.form_values.prototype.webfui$plugin$core$Plugin$declare_events$arity$4 = (function (this$,body,dom_watchers,parsed_html){
var self__ = this;
return body.addEventListener("input",cljs.core.partial.call(null,webfui.plugin.form_values.input,dom_watchers,parsed_html));
});
webfui.plugin.form_values.form_values.prototype.webfui$plugin$core$Plugin$fix_dom$arity$1 = (function (this$){
var self__ = this;
return null;
});
