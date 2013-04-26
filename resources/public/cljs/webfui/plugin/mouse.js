goog.provide('webfui.plugin.mouse');
goog.require('cljs.core');
goog.require('webfui.plugin.core');
goog.require('webfui.dom_manipulation');
goog.require('cljs.reader');
goog.require('webfui.utilities');
goog.require('cljs.reader');
goog.require('webfui.dom_manipulation');
goog.require('webfui.utilities');
goog.require('webfui.plugin.core');
webfui.plugin.mouse.mouse_watchers = cljs.core.atom.call(null,cljs.core.ObjMap.EMPTY);
webfui.plugin.mouse.nodelist_to_seq = (function nodelist_to_seq(nl){
var result_seq = cljs.core.map.call(null,(function (p1__31278_SHARP_){
return nl.item(p1__31278_SHARP_);
}),cljs.core.range.call(null,nl.length));
return result_seq;
});
webfui.plugin.mouse.offset = (function offset(node){
var op = node.offsetParent;
if(cljs.core.truth_(op))
{var vec__31280 = offset.call(null,op);
var x = cljs.core.nth.call(null,vec__31280,0,null);
var y = cljs.core.nth.call(null,vec__31280,1,null);
return cljs.core.PersistentVector.fromArray([(x + node.offsetLeft),(y + node.offsetTop)], true);
} else
{return cljs.core.PersistentVector.fromArray([0,0], true);
}
});
webfui.plugin.mouse.all_elements_at_point = (function all_elements_at_point(client_point){
return (function f(element){
var vec__31284 = client_point;
var x = cljs.core.nth.call(null,vec__31284,0,null);
var y = cljs.core.nth.call(null,vec__31284,1,null);
var chi = cljs.core.mapcat.call(null,f,webfui.plugin.mouse.nodelist_to_seq.call(null,element.childNodes));
if(cljs.core.truth_((function (){var and__3941__auto__ = element.getBoundingClientRect;
if(cljs.core.truth_(and__3941__auto__))
{var rect = element.getBoundingClientRect();
var and__3941__auto____$1 = (rect.left <= x);
if(and__3941__auto____$1)
{var and__3941__auto____$2 = (rect.top <= y);
if(and__3941__auto____$2)
{var and__3941__auto____$3 = (rect.right > x);
if(and__3941__auto____$3)
{return (rect.bottom > y);
} else
{return and__3941__auto____$3;
}
} else
{return and__3941__auto____$2;
}
} else
{return and__3941__auto____$1;
}
} else
{return and__3941__auto__;
}
})()))
{return cljs.core.cons.call(null,element,chi);
} else
{return chi;
}
}).call(null,webfui.utilities.body.call(null));
});
webfui.plugin.mouse.merge_data = (function merge_data(acc,lst){
while(true){
var temp__4090__auto__ = lst;
if(cljs.core.truth_(temp__4090__auto__))
{var vec__31286 = temp__4090__auto__;
var k = cljs.core.nth.call(null,vec__31286,0,null);
var more = cljs.core.nthnext.call(null,vec__31286,1);
if(cljs.core.not.call(null,k))
{{
var G__31287 = acc;
var G__31288 = more;
acc = G__31287;
lst = G__31288;
continue;
}
} else
{if(cljs.core.not.call(null,acc))
{{
var G__31289 = k;
var G__31290 = more;
acc = G__31289;
lst = G__31290;
continue;
}
} else
{if((function (){var and__3941__auto__ = cljs.core.map_QMARK_.call(null,k);
if(and__3941__auto__)
{return cljs.core.map_QMARK_.call(null,acc);
} else
{return and__3941__auto__;
}
})())
{{
var G__31291 = cljs.core.merge.call(null,acc,k);
var G__31292 = more;
acc = G__31291;
lst = G__31292;
continue;
}
} else
{if("\uFDD0'else")
{{
var G__31293 = k;
var G__31294 = more;
acc = G__31293;
lst = G__31294;
continue;
}
} else
{return null;
}
}
}
}
} else
{return acc;
}
break;
}
});
webfui.plugin.mouse.mouse_element = (function mouse_element(parsed_html,ev){
var target = ev.target;
var typ = ev.type;
var point = (cljs.core.truth_(cljs.core.PersistentHashSet.fromArray(["touchstart"]).call(null,typ))?(function (){var touch = ev.touches.item(0);
return cljs.core.PersistentVector.fromArray([touch.clientX,touch.clientY], true);
})():(cljs.core.truth_(cljs.core.PersistentHashSet.fromArray(["touchmove"]).call(null,typ))?(function (){var touch = ev.touches.item(0);
return cljs.core.PersistentVector.fromArray([touch.clientX,touch.clientY], true);
})():((cljs.core._EQ_.call(null,"touchend",typ))?(function (){var touch = ev.changedTouches.item(0);
return cljs.core.PersistentVector.fromArray([touch.clientX,touch.clientY], true);
})():(("\uFDD0'else")?cljs.core.PersistentVector.fromArray([ev.clientX,ev.clientY], true):null))));
var elements = webfui.plugin.mouse.all_elements_at_point.call(null,point);
var data = webfui.plugin.mouse.merge_data.call(null,null,(function (){var iter__12691__auto__ = (function iter__31297(s__31298){
return (new cljs.core.LazySeq(null,false,(function (){
var s__31298__$1 = s__31298;
while(true){
var temp__4092__auto__ = cljs.core.seq.call(null,s__31298__$1);
if(temp__4092__auto__)
{var xs__4579__auto__ = temp__4092__auto__;
var element = cljs.core.first.call(null,xs__4579__auto__);
return cljs.core.cons.call(null,(function (){var temp__4092__auto____$1 = element.getAttribute("data");
if(cljs.core.truth_(temp__4092__auto____$1))
{var s = temp__4092__auto____$1;
return cljs.reader.read_string.call(null,s);
} else
{return null;
}
})(),iter__31297.call(null,cljs.core.rest.call(null,s__31298__$1)));
} else
{return null;
}
break;
}
}),null));
});
return iter__12691__auto__.call(null,elements);
})());
return cljs.core.PersistentVector.fromArray([cljs.core.update_in.call(null,webfui.dom_manipulation.resolve_target.call(null,cljs.core.deref.call(null,parsed_html),target),cljs.core.PersistentVector.fromArray([1], true),cljs.core.assoc,"\uFDD0'offset",webfui.plugin.mouse.offset.call(null,target),"\uFDD0'data",data),cljs.core.PersistentVector.fromArray([ev.pageX,ev.pageY], true)], true);
});
webfui.plugin.mouse.update_offset = (function update_offset(element,target){
return cljs.core.update_in.call(null,element,cljs.core.PersistentVector.fromArray([1], true),(function (attr){
return cljs.core.assoc.call(null,attr,"\uFDD0'offset",webfui.plugin.mouse.offset.call(null,target),"\uFDD0'data",(function (){var temp__4090__auto__ = target.getAttribute("data");
if(cljs.core.truth_(temp__4090__auto__))
{var data = temp__4090__auto__;
return cljs.reader.read_string.call(null,data);
} else
{return (new cljs.core.Keyword("\uFDD0'data")).call(null,attr);
}
})());
}));
});
webfui.plugin.mouse.mouse_event = (function mouse_event(element){
return cljs.core.deref.call(null,webfui.plugin.mouse.mouse_watchers).call(null,cljs.core.get_in.call(null,element,cljs.core.PersistentVector.fromArray([1,"\uFDD0'mouse"], true)));
});
webfui.plugin.mouse.add_mouse_watch = (function add_mouse_watch(id,fun){
return cljs.core.swap_BANG_.call(null,webfui.plugin.mouse.mouse_watchers,cljs.core.assoc,id,fun);
});
webfui.plugin.mouse.mouse_down = (function mouse_down(parsed_html,ev){
var target = ev.target;
var vec__31300 = webfui.plugin.mouse.mouse_element.call(null,parsed_html,ev);
var new_element = cljs.core.nth.call(null,vec__31300,0,null);
var point = cljs.core.nth.call(null,vec__31300,1,null);
var event = webfui.plugin.mouse.mouse_event.call(null,new_element);
if(cljs.core.truth_(event))
{var new_element__$1 = cljs.core.assoc_in.call(null,new_element,cljs.core.PersistentVector.fromArray([1,"\uFDD0'active"], true),true);
webfui.plugin.mouse.mouse_down_element = new_element__$1;
webfui.plugin.mouse.mouse_down_target = target;
webfui.plugin.mouse.points = cljs.core.PersistentVector.fromArray([point], true);
return event.call(null,new_element__$1,new_element__$1,webfui.plugin.mouse.points);
} else
{return null;
}
});
webfui.plugin.mouse.mouse_move = (function mouse_move(parsed_html,ev){
ev.preventDefault();
if(cljs.core.truth_(webfui.plugin.mouse.mouse_down_element))
{var target = ev.target;
var vec__31303 = webfui.plugin.mouse.mouse_element.call(null,parsed_html,ev);
var new_element = cljs.core.nth.call(null,vec__31303,0,null);
var point = cljs.core.nth.call(null,vec__31303,1,null);
var event = webfui.plugin.mouse.mouse_event.call(null,webfui.plugin.mouse.mouse_down_element);
webfui.plugin.mouse.points = cljs.core.conj.call(null,webfui.plugin.mouse.points,point);
return event.call(null,webfui.plugin.mouse.update_offset.call(null,webfui.plugin.mouse.mouse_down_element,webfui.plugin.mouse.mouse_down_target),new_element,webfui.plugin.mouse.points);
} else
{return null;
}
});
webfui.plugin.mouse.mouse_up = (function mouse_up(parsed_html,ev){
var target = ev.target;
var vec__31305 = webfui.plugin.mouse.mouse_element.call(null,parsed_html,ev);
var new_element = cljs.core.nth.call(null,vec__31305,0,null);
var point = cljs.core.nth.call(null,vec__31305,1,null);
if(cljs.core.truth_(webfui.plugin.mouse.mouse_down_element))
{var event = webfui.plugin.mouse.mouse_event.call(null,webfui.plugin.mouse.mouse_down_element);
var first_element = cljs.core.update_in.call(null,webfui.plugin.mouse.mouse_down_element,cljs.core.PersistentVector.fromArray([1], true),(function (p1__31301_SHARP_){
return cljs.core.dissoc.call(null,p1__31301_SHARP_,"\uFDD0'active");
}));
event.call(null,webfui.plugin.mouse.update_offset.call(null,first_element,webfui.plugin.mouse.mouse_down_target),new_element,webfui.plugin.mouse.points);
webfui.plugin.mouse.points = null;
webfui.plugin.mouse.mouse_down_element = null;
webfui.plugin.mouse.mouse_down_target = null;
} else
{return null;
}
});
goog.provide('webfui.plugin.mouse.mouse');

/**
* @constructor
*/
webfui.plugin.mouse.mouse = (function (){
})
webfui.plugin.mouse.mouse.cljs$lang$type = true;
webfui.plugin.mouse.mouse.cljs$lang$ctorPrSeq = (function (this__12534__auto__){
return cljs.core.list.call(null,"webfui.plugin.mouse/mouse");
});
webfui.plugin.mouse.mouse.cljs$lang$ctorPrWriter = (function (this__12534__auto__,writer__12535__auto__,opt__12536__auto__){
return cljs.core._write.call(null,writer__12535__auto__,"webfui.plugin.mouse/mouse");
});
webfui.plugin.mouse.mouse.prototype.webfui$plugin$core$Plugin$ = true;
webfui.plugin.mouse.mouse.prototype.webfui$plugin$core$Plugin$declare_events$arity$4 = (function (this$,body,dom_watchers,parsed_html){
var self__ = this;
window.setTimeout((function (){
return window.scrollTo(0,1);
}),100);
body.addEventListener("mousedown",cljs.core.partial.call(null,webfui.plugin.mouse.mouse_down,parsed_html));
body.addEventListener("mousemove",cljs.core.partial.call(null,webfui.plugin.mouse.mouse_move,parsed_html));
body.addEventListener("mouseup",cljs.core.partial.call(null,webfui.plugin.mouse.mouse_up,parsed_html));
window.addEventListener("touchstart",cljs.core.partial.call(null,webfui.plugin.mouse.mouse_down,parsed_html));
window.addEventListener("touchmove",cljs.core.partial.call(null,webfui.plugin.mouse.mouse_move,parsed_html));
return window.addEventListener("touchend",cljs.core.partial.call(null,webfui.plugin.mouse.mouse_up,parsed_html));
});
webfui.plugin.mouse.mouse.prototype.webfui$plugin$core$Plugin$fix_dom$arity$1 = (function (this$){
var self__ = this;
return null;
});
