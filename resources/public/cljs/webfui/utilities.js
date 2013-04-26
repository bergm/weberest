goog.provide('webfui.utilities');
goog.require('cljs.core');
webfui.utilities.body = (function body(){
return document.body;
});
webfui.utilities.get_attribute = (function get_attribute(element,key){
return cljs.core.get_in.call(null,element,cljs.core.PersistentVector.fromArray([1,key], true));
});
webfui.utilities.clicked = (function clicked(first_element,last_element){
var and__3941__auto__ = cljs.core._EQ_.call(null,first_element,last_element);
if(and__3941__auto__)
{return cljs.core.not.call(null,webfui.utilities.get_attribute.call(null,first_element,"\uFDD0'active"));
} else
{return and__3941__auto__;
}
});
