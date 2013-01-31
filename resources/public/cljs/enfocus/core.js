goog.provide('enfocus.core');
goog.require('cljs.core');
goog.require('goog.dom.query');
goog.require('goog.async.Delay');
goog.require('goog.dom.classes');
goog.require('goog.dom.ViewportSizeMonitor');
goog.require('goog.events');
goog.require('enfocus.enlive.syntax');
goog.require('goog.dom');
goog.require('clojure.string');
goog.require('goog.fx.dom');
goog.require('goog.Timer');
goog.require('goog.style');
goog.require('domina');
goog.require('goog.net.XhrIo');
goog.require('domina.css');
goog.require('goog.fx');
enfocus.core.debug = false;
enfocus.core.log_debug = (function log_debug(mesg){
if(cljs.core.truth_((function (){var and__3822__auto__ = enfocus.core.debug;
if(cljs.core.truth_(and__3822__auto__))
{return !(cljs.core._EQ_.call(null,window.console,undefined));
} else
{return and__3822__auto__;
}
})()))
{return console.log(mesg);
} else
{return null;
}
});
enfocus.core.setTimeout = (function setTimeout(func,ttime){
return goog.Timer.callOnce(func,ttime);
});
enfocus.core.node_QMARK_ = (function node_QMARK_(tst){
return goog.dom.isNodeLike(tst);
});
enfocus.core.nodelist_QMARK_ = (function nodelist_QMARK_(tst){
return cljs.core.instance_QMARK_.call(null,NodeList,tst);
});
/**
* coverts a nodelist, node into a collection
*/
enfocus.core.nodes__GT_coll = (function nodes__GT_coll(nl){
if(cljs.core._EQ_.call(null,nl,window))
{return cljs.core.PersistentVector.fromArray([nl], true);
} else
{return domina.nodes.call(null,nl);
}
});
enfocus.core.flatten_nodes_coll = (function flatten_nodes_coll(values){
return cljs.core.mapcat.call(null,(function (p1__4970_SHARP_){
if(cljs.core.string_QMARK_.call(null,p1__4970_SHARP_))
{return cljs.core.PersistentVector.fromArray([goog.dom.createTextNode(p1__4970_SHARP_)], true);
} else
{if("\uFDD0'else")
{return enfocus.core.nodes__GT_coll.call(null,p1__4970_SHARP_);
} else
{return null;
}
}
}),values);
});
/**
* Sets property name to a value on a element and	Returns the original object
*/
enfocus.core.style_set = (function style_set(obj,values){
var G__4973_4975 = cljs.core.seq.call(null,cljs.core.apply.call(null,cljs.core.hash_map,values));
while(true){
if(G__4973_4975)
{var vec__4974_4976 = cljs.core.first.call(null,G__4973_4975);
var attr_4977 = cljs.core.nth.call(null,vec__4974_4976,0,null);
var value_4978 = cljs.core.nth.call(null,vec__4974_4976,1,null);
goog.style.setStyle(obj,cljs.core.name.call(null,attr_4977),value_4978);
{
var G__4979 = cljs.core.next.call(null,G__4973_4975);
G__4973_4975 = G__4979;
continue;
}
} else
{}
break;
}
return obj;
});
/**
* removes the property value from an elements style obj.
*/
enfocus.core.style_remove = (function style_remove(obj,values){
var G__4981 = cljs.core.seq.call(null,values);
while(true){
if(G__4981)
{var attr = cljs.core.first.call(null,G__4981);
if(cljs.core.truth_(goog.userAgent.IE))
{goog.style.setStyle(obj,cljs.core.name.call(null,attr),"");
} else
{obj.style.removeProperty(cljs.core.name.call(null,attr));
}
{
var G__4982 = cljs.core.next.call(null,G__4981);
G__4981 = G__4982;
continue;
}
} else
{return null;
}
break;
}
});
enfocus.core.get_eff_prop_name = (function get_eff_prop_name(etype){
return [cljs.core.str("__ef_effect_"),cljs.core.str(etype)].join('');
});
enfocus.core.get_mills = (function get_mills(){
return (new Date()).getMilliseconds();
});
/**
* returns true if the node(child) is a child of parent
*/
enfocus.core.child_of_QMARK_ = (function child_of_QMARK_(parent,child){
while(true){
if(cljs.core.not.call(null,child))
{return false;
} else
{if((parent === child))
{return false;
} else
{if((child.parentNode === parent))
{return true;
} else
{if("\uFDD0'else")
{{
var G__4983 = parent;
var G__4984 = child.parentNode;
parent = G__4983;
child = G__4984;
continue;
}
} else
{return null;
}
}
}
}
break;
}
});
/**
* this is used to build cross browser versions of :mouseenter and :mouseleave events
*/
enfocus.core.mouse_enter_leave = (function mouse_enter_leave(func){
return (function (e){
var re = e.relatedTarget;
var this$ = e.currentTarget;
if((function (){var and__3822__auto__ = !((re === this$));
if(and__3822__auto__)
{return cljs.core.not.call(null,enfocus.core.child_of_QMARK_.call(null,this$,re));
} else
{return and__3822__auto__;
}
})())
{return func.call(null,e);
} else
{return null;
}
});
});
enfocus.core.pix_round = (function pix_round(step){
if((step < 0))
{return Math.floor.call(null,step);
} else
{return Math.ceil.call(null,step);
}
});
enfocus.core.add_map_attrs = (function() {
var add_map_attrs = null;
var add_map_attrs__2 = (function (elem,ats){
if(cljs.core.truth_(elem))
{if(cljs.core.map_QMARK_.call(null,ats))
{var G__4987_4989 = cljs.core.seq.call(null,ats);
while(true){
if(G__4987_4989)
{var vec__4988_4990 = cljs.core.first.call(null,G__4987_4989);
var k_4991 = cljs.core.nth.call(null,vec__4988_4990,0,null);
var v_4992 = cljs.core.nth.call(null,vec__4988_4990,1,null);
add_map_attrs.call(null,elem,k_4991,v_4992);
{
var G__4993 = cljs.core.next.call(null,G__4987_4989);
G__4987_4989 = G__4993;
continue;
}
} else
{}
break;
}
return elem;
} else
{return null;
}
} else
{return null;
}
});
var add_map_attrs__3 = (function (elem,k,v){
elem.setAttribute(cljs.core.name.call(null,k),v);
return elem;
});
add_map_attrs = function(elem,k,v){
switch(arguments.length){
case 2:
return add_map_attrs__2.call(this,elem,k);
case 3:
return add_map_attrs__3.call(this,elem,k,v);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
add_map_attrs.cljs$lang$arity$2 = add_map_attrs__2;
add_map_attrs.cljs$lang$arity$3 = add_map_attrs__3;
return add_map_attrs;
})()
;
/**
* this is incremented everytime a remote template is loaded and decremented when
* it is added to the dom cache
*/
enfocus.core.tpl_load_cnt = cljs.core.atom.call(null,0);
/**
* cache for the remote templates
*/
enfocus.core.tpl_cache = cljs.core.atom.call(null,cljs.core.ObjMap.EMPTY);
enfocus.core.hide_style = cljs.core.ObjMap.fromObject(["style"],{"style":"display: none; width: 0px; height: 0px"}).strobj;
/**
* Add a hidden div to hold the dom as we are transforming it this has to be done
* because css selectors do not work unless we have it in the main dom
*/
enfocus.core.create_hidden_dom = (function create_hidden_dom(child){
var div = goog.dom.createDom("div",enfocus.core.hide_style);
if(cljs.core._EQ_.call(null,child.nodeType,11))
{goog.dom.appendChild(div,child);
} else
{enfocus.core.log_debug.call(null,cljs.core.count.call(null,domina.nodes.call(null,child)));
var G__4995_4996 = cljs.core.seq.call(null,domina.nodes.call(null,child));
while(true){
if(G__4995_4996)
{var node_4997 = cljs.core.first.call(null,G__4995_4996);
goog.dom.appendChild(div,node_4997);
{
var G__4998 = cljs.core.next.call(null,G__4995_4996);
G__4995_4996 = G__4998;
continue;
}
} else
{}
break;
}
}
goog.dom.appendChild(goog.dom.getDocument().body,div);
return div;
});
/**
* removes the hidden div and returns the children
*/
enfocus.core.remove_node_return_child = (function remove_node_return_child(div){
var child = div.childNodes;
var frag = document.createDocumentFragment();
goog.dom.append(frag,child);
goog.dom.removeNode(div);
return frag;
});
/**
* replaces all the ids in a string html fragement/template with a generated
* symbol appended on to a existing id this is done to make sure we don't have
* id colisions during the transformation process
*/
enfocus.core.replace_ids = (function replace_ids(text){
var re = (new RegExp("(<.*?\\sid=['\"])(.*?)(['\"].*?>)","g"));
var sym = [cljs.core.str(cljs.core.name.call(null,cljs.core.gensym.call(null,"id"))),cljs.core.str("_")].join('');
return cljs.core.PersistentVector.fromArray([[cljs.core.str(sym)].join(''),text.replace(re,(function (a,b,c,d){
return [cljs.core.str(b),cljs.core.str(sym),cljs.core.str(c),cljs.core.str(d)].join('');
}))], true);
});
/**
* before adding the dom back into the live dom we reset the masked ids to orig vals
*/
enfocus.core.reset_ids = (function reset_ids(sym,nod){
var id_nodes = enfocus.core.css_select.call(null,nod,"*[id]");
var nod_col = enfocus.core.nodes__GT_coll.call(null,id_nodes);
return cljs.core.doall.call(null,cljs.core.map.call(null,(function (p1__4999_SHARP_){
var id = p1__4999_SHARP_.getAttribute("id");
var rid = id.replace(sym,"");
return p1__4999_SHARP_.setAttribute("id",rid);
}),nod_col));
});
/**
* loads a remote file into the cache, and masks the ids to avoid collisions
*/
enfocus.core.load_remote_dom = (function load_remote_dom(uri,dom_key){
if((cljs.core.deref.call(null,enfocus.core.tpl_cache).call(null,uri) == null))
{cljs.core.swap_BANG_.call(null,enfocus.core.tpl_load_cnt,cljs.core.inc);
var req = (new goog.net.XhrIo());
var callback = (function (req__$1){
var text = req__$1.getResponseText();
var vec__5001 = enfocus.core.replace_ids.call(null,text);
var sym = cljs.core.nth.call(null,vec__5001,0,null);
var txt = cljs.core.nth.call(null,vec__5001,1,null);
return cljs.core.swap_BANG_.call(null,enfocus.core.tpl_cache,cljs.core.assoc,dom_key,cljs.core.PersistentVector.fromArray([sym,txt], true));
});
goog.events.listen(req,goog.net.EventType.COMPLETE,(function (){
callback.call(null,req);
return cljs.core.swap_BANG_.call(null,enfocus.core.tpl_load_cnt,cljs.core.dec);
}));
return req.send(uri,"GET");
} else
{return null;
}
});
enfocus.core.html_to_dom = (function html_to_dom(html){
var dfa = enfocus.core.nodes__GT_coll.call(null,domina.html_to_dom.call(null,html));
var frag = document.createDocumentFragment();
enfocus.core.log_debug.call(null,cljs.core.count.call(null,dfa));
var G__5003_5004 = cljs.core.seq.call(null,dfa);
while(true){
if(G__5003_5004)
{var df_5005 = cljs.core.first.call(null,G__5003_5004);
goog.dom.append(frag,df_5005);
{
var G__5006 = cljs.core.next.call(null,G__5003_5004);
G__5003_5004 = G__5006;
continue;
}
} else
{}
break;
}
return frag;
});
/**
* returns and dom from the cache and symbol used to scope the ids
*/
enfocus.core.get_cached_dom = (function get_cached_dom(uri){
var nod = cljs.core.deref.call(null,enfocus.core.tpl_cache).call(null,uri);
if(cljs.core.truth_(nod))
{return cljs.core.PersistentVector.fromArray([cljs.core.first.call(null,nod),enfocus.core.html_to_dom.call(null,cljs.core.second.call(null,nod))], true);
} else
{return null;
}
});
/**
* returns the cached snippet or creates one and adds it to the cache if needed
*/
enfocus.core.get_cached_snippet = (function get_cached_snippet(uri,sel){
var sel_str = enfocus.core.create_sel_str.call(null,sel);
var cache = cljs.core.deref.call(null,enfocus.core.tpl_cache).call(null,[cljs.core.str(uri),cljs.core.str(sel_str)].join(''));
if(cljs.core.truth_(cache))
{return cljs.core.PersistentVector.fromArray([cljs.core.first.call(null,cache),enfocus.core.html_to_dom.call(null,cljs.core.second.call(null,cache))], true);
} else
{var vec__5008 = enfocus.core.get_cached_dom.call(null,uri);
var sym = cljs.core.nth.call(null,vec__5008,0,null);
var tdom = cljs.core.nth.call(null,vec__5008,1,null);
var dom = enfocus.core.create_hidden_dom.call(null,tdom);
var tsnip = domina.nodes.call(null,enfocus.core.css_select.call(null,sym,dom,sel));
var snip = cljs.core.first.call(null,tsnip);
enfocus.core.remove_node_return_child.call(null,dom);
cljs.core.swap_BANG_.call(null,enfocus.core.tpl_cache,cljs.core.assoc,[cljs.core.str(uri),cljs.core.str(sel_str)].join(''),cljs.core.PersistentVector.fromArray([sym,snip.outerHTML], true));
return cljs.core.PersistentVector.fromArray([sym,snip], true);
}
});
/**
* wrapper function for extractors that maps the extraction to all nodes returned by the selector
*/
enfocus.core.extr_multi_node = (function extr_multi_node(func){
return (function trans(pnodes){
var pnod_col = enfocus.core.nodes__GT_coll.call(null,pnodes);
var result = cljs.core.map.call(null,func,pnod_col);
if((cljs.core.count.call(null,result) <= 1))
{return cljs.core.first.call(null,result);
} else
{return result;
}
});
});
/**
* wrapper function for transforms, maps the transform to all nodes returned
* by the selector and provides the ability to chain transforms with the chain command.
*/
enfocus.core.chainable_standard = (function chainable_standard(func){
return (function() {
var trans = null;
var trans__1 = (function (pnodes){
return trans.call(null,pnodes,null);
});
var trans__2 = (function (pnodes,chain){
var pnod_col = enfocus.core.nodes__GT_coll.call(null,pnodes);
cljs.core.doall.call(null,cljs.core.map.call(null,func,pnod_col));
if(!((chain == null)))
{return chain.call(null,pnodes);
} else
{return null;
}
});
trans = function(pnodes,chain){
switch(arguments.length){
case 1:
return trans__1.call(this,pnodes);
case 2:
return trans__2.call(this,pnodes,chain);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
trans.cljs$lang$arity$1 = trans__1;
trans.cljs$lang$arity$2 = trans__2;
return trans;
})()
});
/**
* wrapper function for effects, maps the effect to all nodes returned by the
* selector and provides chaining and callback functionality
*/
enfocus.core.chainable_effect = (function chainable_effect(func,callback){
return (function() {
var trans = null;
var trans__1 = (function (pnodes){
return trans.call(null,pnodes,null);
});
var trans__2 = (function (pnodes,chain){
var pnod_col = enfocus.core.nodes__GT_coll.call(null,pnodes);
var cnt = cljs.core.atom.call(null,cljs.core.count.call(null,pnod_col));
var partial_cback = (function (){
cljs.core.swap_BANG_.call(null,cnt,cljs.core.dec);
if(cljs.core._EQ_.call(null,0,cljs.core.deref.call(null,cnt)))
{if(!((callback == null)))
{callback.call(null,pnodes);
} else
{}
if(!((chain == null)))
{return chain.call(null,pnodes);
} else
{return null;
}
} else
{return null;
}
});
var G__5013 = cljs.core.seq.call(null,pnod_col);
while(true){
if(G__5013)
{var pnod = cljs.core.first.call(null,G__5013);
func.call(null,pnod,partial_cback);
{
var G__5014 = cljs.core.next.call(null,G__5013);
G__5013 = G__5014;
continue;
}
} else
{return null;
}
break;
}
});
trans = function(pnodes,chain){
switch(arguments.length){
case 1:
return trans__1.call(this,pnodes);
case 2:
return trans__2.call(this,pnodes,chain);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
trans.cljs$lang$arity$1 = trans__1;
trans.cljs$lang$arity$2 = trans__2;
return trans;
})()
});
/**
* Allows standard domina functions to be chainable
*/
enfocus.core.domina_chain = (function() {
var domina_chain = null;
var domina_chain__1 = (function (func){
return (function() {
var trans = null;
var trans__1 = (function (nodes){
return trans.call(null,nodes,null);
});
var trans__2 = (function (nodes,chain){
func.call(null,nodes);
if(!((chain == null)))
{return chain.call(null,nodes);
} else
{return null;
}
});
trans = function(nodes,chain){
switch(arguments.length){
case 1:
return trans__1.call(this,nodes);
case 2:
return trans__2.call(this,nodes,chain);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
trans.cljs$lang$arity$1 = trans__1;
trans.cljs$lang$arity$2 = trans__2;
return trans;
})()
});
var domina_chain__2 = (function (values,func){
return (function() {
var trans = null;
var trans__1 = (function (nodes){
return trans.call(null,nodes,null);
});
var trans__2 = (function (nodes,chain){
var vnodes_5017 = cljs.core.mapcat.call(null,(function (p1__5009_SHARP_){
return domina.nodes.call(null,p1__5009_SHARP_);
}),values);
func.call(null,nodes,vnodes_5017);
if(!((chain == null)))
{return chain.call(null,nodes);
} else
{return null;
}
});
trans = function(nodes,chain){
switch(arguments.length){
case 1:
return trans__1.call(this,nodes);
case 2:
return trans__2.call(this,nodes,chain);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
trans.cljs$lang$arity$1 = trans__1;
trans.cljs$lang$arity$2 = trans__2;
return trans;
})()
});
domina_chain = function(values,func){
switch(arguments.length){
case 1:
return domina_chain__1.call(this,values);
case 2:
return domina_chain__2.call(this,values,func);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
domina_chain.cljs$lang$arity$1 = domina_chain__1;
domina_chain.cljs$lang$arity$2 = domina_chain__2;
return domina_chain;
})()
;
/**
* Replaces the content of the element. Values can be nodes or collection of nodes.
* @param {...*} var_args
*/
enfocus.core.en_content = (function() { 
var en_content__delegate = function (values){
return enfocus.core.domina_chain.call(null,values,(function (p1__5015_SHARP_,p2__5016_SHARP_){
domina.destroy_children_BANG_.call(null,p1__5015_SHARP_);
return domina.append_BANG_.call(null,p1__5015_SHARP_,p2__5016_SHARP_);
}));
};
var en_content = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_content__delegate.call(this, values);
};
en_content.cljs$lang$maxFixedArity = 0;
en_content.cljs$lang$applyTo = (function (arglist__5019){
var values = cljs.core.seq(arglist__5019);;
return en_content__delegate(values);
});
en_content.cljs$lang$arity$variadic = en_content__delegate;
return en_content;
})()
;
/**
* Replaces the content of the element with the dom structure represented by the html string passed
*/
enfocus.core.en_html_content = (function en_html_content(txt){
return enfocus.core.domina_chain.call(null,(function (p1__5018_SHARP_){
return domina.set_html_BANG_.call(null,p1__5018_SHARP_,txt);
}));
});
/**
* Assocs attributes and values on the selected element.
* @param {...*} var_args
*/
enfocus.core.en_set_attr = (function() { 
var en_set_attr__delegate = function (values){
var pairs = cljs.core.partition.call(null,2,values);
return enfocus.core.domina_chain.call(null,(function (p1__5020_SHARP_){
var G__5024 = cljs.core.seq.call(null,pairs);
while(true){
if(G__5024)
{var vec__5025 = cljs.core.first.call(null,G__5024);
var name = cljs.core.nth.call(null,vec__5025,0,null);
var value = cljs.core.nth.call(null,vec__5025,1,null);
domina.set_attr_BANG_.call(null,p1__5020_SHARP_,name,value);
{
var G__5026 = cljs.core.next.call(null,G__5024);
G__5024 = G__5026;
continue;
}
} else
{return null;
}
break;
}
}));
};
var en_set_attr = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_set_attr__delegate.call(this, values);
};
en_set_attr.cljs$lang$maxFixedArity = 0;
en_set_attr.cljs$lang$applyTo = (function (arglist__5027){
var values = cljs.core.seq(arglist__5027);;
return en_set_attr__delegate(values);
});
en_set_attr.cljs$lang$arity$variadic = en_set_attr__delegate;
return en_set_attr;
})()
;
/**
* Dissocs attributes on the selected element.
* @param {...*} var_args
*/
enfocus.core.en_remove_attr = (function() { 
var en_remove_attr__delegate = function (values){
return enfocus.core.domina_chain.call(null,(function (p1__5021_SHARP_){
var G__5029 = cljs.core.seq.call(null,values);
while(true){
if(G__5029)
{var name = cljs.core.first.call(null,G__5029);
domina.remove_attr_BANG_.call(null,p1__5021_SHARP_,name);
{
var G__5030 = cljs.core.next.call(null,G__5029);
G__5029 = G__5030;
continue;
}
} else
{return null;
}
break;
}
}));
};
var en_remove_attr = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_remove_attr__delegate.call(this, values);
};
en_remove_attr.cljs$lang$maxFixedArity = 0;
en_remove_attr.cljs$lang$applyTo = (function (arglist__5031){
var values = cljs.core.seq(arglist__5031);;
return en_remove_attr__delegate(values);
});
en_remove_attr.cljs$lang$arity$variadic = en_remove_attr__delegate;
return en_remove_attr;
})()
;
/**
* @param {...*} var_args
*/
enfocus.core.en_set_prop = (function() { 
var en_set_prop__delegate = function (forms){
return enfocus.core.chainable_standard.call(null,(function (node){
var h = cljs.core.mapcat.call(null,(function (p__5034){
var vec__5035 = p__5034;
var n = cljs.core.nth.call(null,vec__5035,0,null);
var v = cljs.core.nth.call(null,vec__5035,1,null);
return cljs.core.list.call(null,cljs.core.name.call(null,n),v);
}),cljs.core.partition.call(null,2,forms));
return goog.dom.setProperties(node,cljs.core.apply.call(null,cljs.core.js_obj,h));
}));
};
var en_set_prop = function (var_args){
var forms = null;
if (goog.isDef(var_args)) {
  forms = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_set_prop__delegate.call(this, forms);
};
en_set_prop.cljs$lang$maxFixedArity = 0;
en_set_prop.cljs$lang$applyTo = (function (arglist__5036){
var forms = cljs.core.seq(arglist__5036);;
return en_set_prop__delegate(forms);
});
en_set_prop.cljs$lang$arity$variadic = en_set_prop__delegate;
return en_set_prop;
})()
;
/**
* returns true if the element has a given class
*/
enfocus.core.has_class = (function has_class(el,cls){
return goog.dom.classes.hasClass(el,cls);
});
/**
* Adds the specified classes to the selected element.
* @param {...*} var_args
*/
enfocus.core.en_add_class = (function() { 
var en_add_class__delegate = function (values){
return enfocus.core.domina_chain.call(null,(function (p1__5037_SHARP_){
var G__5040 = cljs.core.seq.call(null,values);
while(true){
if(G__5040)
{var val = cljs.core.first.call(null,G__5040);
domina.add_class_BANG_.call(null,p1__5037_SHARP_,val);
{
var G__5041 = cljs.core.next.call(null,G__5040);
G__5040 = G__5041;
continue;
}
} else
{return null;
}
break;
}
}));
};
var en_add_class = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_add_class__delegate.call(this, values);
};
en_add_class.cljs$lang$maxFixedArity = 0;
en_add_class.cljs$lang$applyTo = (function (arglist__5042){
var values = cljs.core.seq(arglist__5042);;
return en_add_class__delegate(values);
});
en_add_class.cljs$lang$arity$variadic = en_add_class__delegate;
return en_add_class;
})()
;
/**
* Removes the specified classes from the selected element.
* @param {...*} var_args
*/
enfocus.core.en_remove_class = (function() { 
var en_remove_class__delegate = function (values){
return enfocus.core.domina_chain.call(null,(function (p1__5038_SHARP_){
var G__5045 = cljs.core.seq.call(null,values);
while(true){
if(G__5045)
{var val = cljs.core.first.call(null,G__5045);
domina.remove_class_BANG_.call(null,p1__5038_SHARP_,val);
{
var G__5046 = cljs.core.next.call(null,G__5045);
G__5045 = G__5046;
continue;
}
} else
{return null;
}
break;
}
}));
};
var en_remove_class = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_remove_class__delegate.call(this, values);
};
en_remove_class.cljs$lang$maxFixedArity = 0;
en_remove_class.cljs$lang$applyTo = (function (arglist__5047){
var values = cljs.core.seq(arglist__5047);;
return en_remove_class__delegate(values);
});
en_remove_class.cljs$lang$arity$variadic = en_remove_class__delegate;
return en_remove_class;
})()
;
/**
* Sets the specified classes on the selected element
* @param {...*} var_args
*/
enfocus.core.en_set_class = (function() { 
var en_set_class__delegate = function (values){
return enfocus.core.domina_chain.call(null,(function (p1__5043_SHARP_){
return domina.set_classes_BANG_.call(null,p1__5043_SHARP_,values);
}));
};
var en_set_class = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_set_class__delegate.call(this, values);
};
en_set_class.cljs$lang$maxFixedArity = 0;
en_set_class.cljs$lang$applyTo = (function (arglist__5048){
var values = cljs.core.seq(arglist__5048);;
return en_set_class__delegate(values);
});
en_set_class.cljs$lang$arity$variadic = en_set_class__delegate;
return en_set_class;
})()
;
/**
* @param {...*} var_args
*/
enfocus.core.en_do__GT_ = (function() { 
var en_do__GT___delegate = function (forms){
return enfocus.core.chainable_standard.call(null,(function (pnod){
var G__5052 = cljs.core.seq.call(null,forms);
while(true){
if(G__5052)
{var fun = cljs.core.first.call(null,G__5052);
fun.call(null,pnod);
{
var G__5053 = cljs.core.next.call(null,G__5052);
G__5052 = G__5053;
continue;
}
} else
{return null;
}
break;
}
}));
};
var en_do__GT_ = function (var_args){
var forms = null;
if (goog.isDef(var_args)) {
  forms = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_do__GT___delegate.call(this, forms);
};
en_do__GT_.cljs$lang$maxFixedArity = 0;
en_do__GT_.cljs$lang$applyTo = (function (arglist__5054){
var forms = cljs.core.seq(arglist__5054);;
return en_do__GT___delegate(forms);
});
en_do__GT_.cljs$lang$arity$variadic = en_do__GT___delegate;
return en_do__GT_;
})()
;
/**
* Appends the content of the element. Values can be nodes or collection of nodes.
* @param {...*} var_args
*/
enfocus.core.en_append = (function() { 
var en_append__delegate = function (values){
return enfocus.core.domina_chain.call(null,values,(function (p1__5049_SHARP_,p2__5050_SHARP_){
return domina.append_BANG_.call(null,p1__5049_SHARP_,p2__5050_SHARP_);
}));
};
var en_append = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_append__delegate.call(this, values);
};
en_append.cljs$lang$maxFixedArity = 0;
en_append.cljs$lang$applyTo = (function (arglist__5057){
var values = cljs.core.seq(arglist__5057);;
return en_append__delegate(values);
});
en_append.cljs$lang$arity$variadic = en_append__delegate;
return en_append;
})()
;
/**
* Prepends the content of the element. Values can be nodes or collection of nodes.
* @param {...*} var_args
*/
enfocus.core.en_prepend = (function() { 
var en_prepend__delegate = function (values){
return enfocus.core.domina_chain.call(null,values,(function (p1__5055_SHARP_,p2__5056_SHARP_){
return domina.prepend_BANG_.call(null,p1__5055_SHARP_,p2__5056_SHARP_);
}));
};
var en_prepend = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_prepend__delegate.call(this, values);
};
en_prepend.cljs$lang$maxFixedArity = 0;
en_prepend.cljs$lang$applyTo = (function (arglist__5060){
var values = cljs.core.seq(arglist__5060);;
return en_prepend__delegate(values);
});
en_prepend.cljs$lang$arity$variadic = en_prepend__delegate;
return en_prepend;
})()
;
/**
* inserts the content before the selected node. Values can be nodes or collection of nodes
* @param {...*} var_args
*/
enfocus.core.en_before = (function() { 
var en_before__delegate = function (values){
return enfocus.core.domina_chain.call(null,values,(function (p1__5058_SHARP_,p2__5059_SHARP_){
return domina.insert_before_BANG_.call(null,p1__5058_SHARP_,p2__5059_SHARP_);
}));
};
var en_before = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_before__delegate.call(this, values);
};
en_before.cljs$lang$maxFixedArity = 0;
en_before.cljs$lang$applyTo = (function (arglist__5063){
var values = cljs.core.seq(arglist__5063);;
return en_before__delegate(values);
});
en_before.cljs$lang$arity$variadic = en_before__delegate;
return en_before;
})()
;
/**
* inserts the content after the selected node. Values can be nodes or collection of nodes
* @param {...*} var_args
*/
enfocus.core.en_after = (function() { 
var en_after__delegate = function (values){
return enfocus.core.domina_chain.call(null,values,(function (p1__5061_SHARP_,p2__5062_SHARP_){
return domina.insert_after_BANG_.call(null,p1__5061_SHARP_,p2__5062_SHARP_);
}));
};
var en_after = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_after__delegate.call(this, values);
};
en_after.cljs$lang$maxFixedArity = 0;
en_after.cljs$lang$applyTo = (function (arglist__5066){
var values = cljs.core.seq(arglist__5066);;
return en_after__delegate(values);
});
en_after.cljs$lang$arity$variadic = en_after__delegate;
return en_after;
})()
;
/**
* substitutes the content for the selected node. Values can be nodes or collection of nodes
* @param {...*} var_args
*/
enfocus.core.en_substitute = (function() { 
var en_substitute__delegate = function (values){
return enfocus.core.domina_chain.call(null,values,(function (p1__5064_SHARP_,p2__5065_SHARP_){
return domina.swap_content_BANG_.call(null,p1__5064_SHARP_,p2__5065_SHARP_);
}));
};
var en_substitute = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_substitute__delegate.call(this, values);
};
en_substitute.cljs$lang$maxFixedArity = 0;
en_substitute.cljs$lang$applyTo = (function (arglist__5068){
var values = cljs.core.seq(arglist__5068);;
return en_substitute__delegate(values);
});
en_substitute.cljs$lang$arity$variadic = en_substitute__delegate;
return en_substitute;
})()
;
/**
* removes the selected nodes from the dom
*/
enfocus.core.en_remove_node = (function en_remove_node(){
return enfocus.core.domina_chain.call(null,(function (p1__5067_SHARP_){
return domina.detach_BANG_.call(null,p1__5067_SHARP_);
}));
});
/**
* wrap and element in a new element defined as :div {:class 'temp'}
*/
enfocus.core.en_wrap = (function en_wrap(elm,mattr){
return enfocus.core.chainable_standard.call(null,(function (pnod){
var elem = goog.dom.createElement(cljs.core.name.call(null,elm));
enfocus.core.add_map_attrs.call(null,elem,mattr);
enfocus.core.at.call(null,elem,enfocus.core.en_content.call(null,pnod.cloneNode(true)));
return enfocus.core.at.call(null,pnod,enfocus.core.en_do__GT_.call(null,enfocus.core.en_after.call(null,elem),enfocus.core.en_remove_node.call(null)));
}));
});
/**
* replaces a node with all its children
*/
enfocus.core.en_unwrap = (function en_unwrap(){
return enfocus.core.chainable_standard.call(null,(function (pnod){
var frag = document.createDocumentFragment();
goog.dom.append(frag,pnod.childNodes);
return goog.dom.replaceNode(frag,pnod);
}));
});
/**
* set a list of style elements from the selected nodes
* @param {...*} var_args
*/
enfocus.core.en_set_style = (function() { 
var en_set_style__delegate = function (values){
var pairs = cljs.core.partition.call(null,2,values);
return enfocus.core.domina_chain.call(null,(function (p1__5069_SHARP_){
var G__5072 = cljs.core.seq.call(null,pairs);
while(true){
if(G__5072)
{var vec__5073 = cljs.core.first.call(null,G__5072);
var name = cljs.core.nth.call(null,vec__5073,0,null);
var value = cljs.core.nth.call(null,vec__5073,1,null);
domina.set_style_BANG_.call(null,p1__5069_SHARP_,name,value);
{
var G__5074 = cljs.core.next.call(null,G__5072);
G__5072 = G__5074;
continue;
}
} else
{return null;
}
break;
}
}));
};
var en_set_style = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_set_style__delegate.call(this, values);
};
en_set_style.cljs$lang$maxFixedArity = 0;
en_set_style.cljs$lang$applyTo = (function (arglist__5075){
var values = cljs.core.seq(arglist__5075);;
return en_set_style__delegate(values);
});
en_set_style.cljs$lang$arity$variadic = en_set_style__delegate;
return en_set_style;
})()
;
/**
* remove a list style elements from the selected nodes. note: you can only remove styles that are inline
* @param {...*} var_args
*/
enfocus.core.en_remove_style = (function() { 
var en_remove_style__delegate = function (values){
return enfocus.core.chainable_standard.call(null,(function (pnod){
return enfocus.core.style_remove.call(null,pnod,values);
}));
};
var en_remove_style = function (var_args){
var values = null;
if (goog.isDef(var_args)) {
  values = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_remove_style__delegate.call(this, values);
};
en_remove_style.cljs$lang$maxFixedArity = 0;
en_remove_style.cljs$lang$applyTo = (function (arglist__5076){
var values = cljs.core.seq(arglist__5076);;
return en_remove_style__delegate(values);
});
en_remove_style.cljs$lang$arity$variadic = en_remove_style__delegate;
return en_remove_style;
})()
;
/**
* calls the focus function on the selected node
*/
enfocus.core.en_focus = (function en_focus(){
return enfocus.core.chainable_standard.call(null,(function (node){
return node.focus();
}));
});
/**
* calls the blur function on the selected node
*/
enfocus.core.en_blur = (function en_blur(){
return enfocus.core.chainable_standard.call(null,(function (node){
return node.blur();
}));
});
/**
* addes key value pair of data to the selected nodes. Only use clojure data structures when setting
*/
enfocus.core.en_set_data = (function en_set_data(ky,val){
return enfocus.core.domina_chain.call(null,(function (p1__5077_SHARP_){
return domina.set_data_BANG_.call(null,p1__5077_SHARP_,ky,val);
}));
});
enfocus.core.view_port_monitor = cljs.core.atom.call(null,null);
/**
* needed to support window :resize
*/
enfocus.core.get_vp_monitor = (function get_vp_monitor(){
if(cljs.core.truth_(cljs.core.deref.call(null,enfocus.core.view_port_monitor)))
{return cljs.core.deref.call(null,enfocus.core.view_port_monitor);
} else
{cljs.core.swap_BANG_.call(null,enfocus.core.view_port_monitor,(function (){
return (new goog.dom.ViewportSizeMonitor());
}));
return cljs.core.deref.call(null,enfocus.core.view_port_monitor);
}
});
enfocus.core.gen_enter_leave_wrapper = (function gen_enter_leave_wrapper(event){
var obj = (new Object());
obj.listen = (function (elm,func,opt_cap,opt_scope,opt_handler){
var callback = enfocus.core.mouse_enter_leave.call(null,func);
callback.listen = func;
callback.scope = opt_scope;
if(cljs.core.truth_(opt_handler))
{return opt_handler.listen(elm,cljs.core.name.call(null,event),callback);
} else
{return goog.events.listen(elm,cljs.core.name.call(null,event),callback);
}
});
obj.unlisten = (function (elm,func,opt_cap,opt_scope,opt_handler){
var listeners = goog.events.getListeners(elm,cljs.core.name.call(null,event),false);
var G__5079_5080 = cljs.core.seq.call(null,listeners);
while(true){
if(G__5079_5080)
{var obj_5081__$1 = cljs.core.first.call(null,G__5079_5080);
var listener_5082 = obj_5081__$1.listener;
if(cljs.core.truth_((function (){var and__3822__auto__ = (function (){var or__3824__auto__ = cljs.core.not.call(null,func);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{return cljs.core._EQ_.call(null,listener_5082.listen,func);
}
})();
if(cljs.core.truth_(and__3822__auto__))
{var or__3824__auto__ = cljs.core.not.call(null,opt_scope);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{return cljs.core._EQ_.call(null,listener_5082.scope,opt_scope);
}
} else
{return and__3822__auto__;
}
})()))
{if(cljs.core.truth_(opt_handler))
{opt_handler.unlisten(elm,cljs.core.name.call(null,event),listener_5082);
} else
{goog.events.unlisten(elm,cljs.core.name.call(null,event),listener_5082);
}
} else
{}
{
var G__5083 = cljs.core.next.call(null,G__5079_5080);
G__5079_5080 = G__5083;
continue;
}
} else
{}
break;
}
return listeners;
});
return obj;
});
enfocus.core.wrapper_register = cljs.core.ObjMap.fromObject(["\uFDD0'mouseenter","\uFDD0'mouseleave"],{"\uFDD0'mouseenter":enfocus.core.gen_enter_leave_wrapper.call(null,"\uFDD0'mouseover"),"\uFDD0'mouseleave":enfocus.core.gen_enter_leave_wrapper.call(null,"\uFDD0'mouseout")});
/**
* adding an event to the selected nodes
*/
enfocus.core.en_listen = (function en_listen(event,func){
var wrapper = enfocus.core.wrapper_register.call(null,event);
return enfocus.core.chainable_standard.call(null,(function (pnod){
if((function (){var and__3822__auto__ = cljs.core._EQ_.call(null,"\uFDD0'resize",event);
if(and__3822__auto__)
{return (window === pnod);
} else
{return and__3822__auto__;
}
})())
{return goog.events.listen(enfocus.core.get_vp_monitor.call(null),"resize",func);
} else
{if((wrapper == null))
{return goog.events.listen(pnod,cljs.core.name.call(null,event),func);
} else
{return goog.events.listenWithWrapper(pnod,wrapper,func);
}
}
}));
});
/**
* removing all listeners of a given event type from the selected nodes
* @param {...*} var_args
*/
enfocus.core.en_remove_listeners = (function() { 
var en_remove_listeners__delegate = function (event_list){
var get_name = (function (p1__5084_SHARP_){
return cljs.core.name.call(null,((cljs.core._EQ_.call(null,p1__5084_SHARP_,"\uFDD0'mouseenter"))?"\uFDD0'mouseover":((cljs.core._EQ_.call(null,p1__5084_SHARP_,"\uFDD0'mouseleave"))?"\uFDD0'mouseout":(("\uFDD0'else")?p1__5084_SHARP_:null))));
});
return enfocus.core.chainable_standard.call(null,(function (pnod){
var G__5086 = cljs.core.seq.call(null,event_list);
while(true){
if(G__5086)
{var ev = cljs.core.first.call(null,G__5086);
goog.events.removeAll(pnod,get_name.call(null,ev));
{
var G__5087 = cljs.core.next.call(null,G__5086);
G__5086 = G__5087;
continue;
}
} else
{return null;
}
break;
}
}));
};
var en_remove_listeners = function (var_args){
var event_list = null;
if (goog.isDef(var_args)) {
  event_list = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return en_remove_listeners__delegate.call(this, event_list);
};
en_remove_listeners.cljs$lang$maxFixedArity = 0;
en_remove_listeners.cljs$lang$applyTo = (function (arglist__5088){
var event_list = cljs.core.seq(arglist__5088);;
return en_remove_listeners__delegate(event_list);
});
en_remove_listeners.cljs$lang$arity$variadic = en_remove_listeners__delegate;
return en_remove_listeners;
})()
;
/**
* removing a specific event from the selected nodes
*/
enfocus.core.en_unlisten = (function() {
var en_unlisten = null;
var en_unlisten__1 = (function (event){
return enfocus.core.en_remove_listeners.call(null,event);
});
var en_unlisten__2 = (function (event,func){
var wrapper = enfocus.core.wrapper_register.call(null,event);
return enfocus.core.chainable_standard.call(null,(function (pnod){
if((wrapper == null))
{return goog.events.unlisten(pnod,cljs.core.name.call(null,event),func);
} else
{return goog.events.unlistenWithWrapper(pnod,wrapper,func);
}
}));
});
en_unlisten = function(event,func){
switch(arguments.length){
case 1:
return en_unlisten__1.call(this,event);
case 2:
return en_unlisten__2.call(this,event,func);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
en_unlisten.cljs$lang$arity$1 = en_unlisten__1;
en_unlisten.cljs$lang$arity$2 = en_unlisten__2;
return en_unlisten;
})()
;
/**
* fade the selected nodes over a set of steps
*/
enfocus.core.en_fade_out = (function en_fade_out(ttime,callback,accel){
return enfocus.core.chainable_effect.call(null,(function (pnod,pcallback){
var anim = (new goog.fx.dom.FadeOut(pnod,ttime,accel));
if(cljs.core.truth_(pcallback))
{goog.events.listen(anim,goog.fx.Animation.EventType.END,pcallback);
} else
{}
return anim.play();
}),callback);
});
/**
* fade the selected nodes over a set of steps
*/
enfocus.core.en_fade_in = (function en_fade_in(ttime,callback,accel){
return enfocus.core.chainable_effect.call(null,(function (pnod,pcallback){
var anim = (new goog.fx.dom.FadeIn(pnod,ttime,accel));
if(cljs.core.truth_(pcallback))
{goog.events.listen(anim,goog.fx.Animation.EventType.END,pcallback);
} else
{}
return anim.play();
}),callback);
});
/**
* resizes the selected elements to a width and height in px optional time series data
*/
enfocus.core.en_resize = (function en_resize(wth,hgt,ttime,callback,accel){
return enfocus.core.chainable_effect.call(null,(function (pnod,pcallback){
var csize = goog.style.getContentBoxSize(pnod);
var start = [csize.width,csize.height];
var wth__$1 = ((cljs.core._EQ_.call(null,"\uFDD0'curwidth",wth))?csize.width:wth);
var hgt__$1 = ((cljs.core._EQ_.call(null,"\uFDD0'curheight",hgt))?csize.height:hgt);
var end = [wth__$1,hgt__$1];
var anim = (new goog.fx.dom.Resize(pnod,start,end,ttime,accel));
if(cljs.core.truth_(pcallback))
{goog.events.listen(anim,goog.fx.Animation.EventType.END,pcallback);
} else
{}
return anim.play();
}),callback);
});
/**
* moves the selected elements to a x and y in px optional time series data
*/
enfocus.core.en_move = (function en_move(xpos,ypos,ttime,callback,accel){
return enfocus.core.chainable_effect.call(null,(function (pnod,pcallback){
var cpos = goog.style.getPosition(pnod);
var start = [cpos.x,cpos.y];
var xpos__$1 = ((cljs.core._EQ_.call(null,"\uFDD0'curx",xpos))?cpos.x:xpos);
var ypos__$1 = ((cljs.core._EQ_.call(null,"\uFDD0'cury",ypos))?cpos.y:ypos);
var end = [xpos__$1,ypos__$1];
var anim = (new goog.fx.dom.Slide(pnod,start,end,ttime,accel));
if(cljs.core.truth_(pcallback))
{goog.events.listen(anim,goog.fx.Animation.EventType.END,pcallback);
} else
{}
return anim.play();
}),callback);
});
/**
* scrolls selected elements to a x and y in px optional time series data
*/
enfocus.core.en_scroll = (function en_scroll(xpos,ypos,ttime,callback,accel){
return ef.chainable_effect.call(null,(function (pnod,pcallback){
var start = [pnod.scrollLeft,pnod.scrollTop];
var xpos__$1 = ((cljs.core._EQ_.call(null,"\uFDD0'curx",xpos))?pnod.scrollLeft:xpos);
var ypos__$1 = ((cljs.core._EQ_.call(null,"\uFDD0'cury",ypos))?pnod.scrollTop:ypos);
var end = [xpos__$1,ypos__$1];
var anim = (new goog.fx.dom.Scroll(pnod,start,end,ttime,accel));
util.log.call(null,[cljs.core.str(start)].join(''),[cljs.core.str(end)].join(''));
if(cljs.core.truth_(pcallback))
{goog.events.listen(anim,goog.fx.Animation.EventType.END,pcallback);
} else
{}
return anim.play();
}),callback);
});
/**
* returns the attribute on the selected element or elements.
* in cases where more than one element is selected you will
* receive a list of values
*/
enfocus.core.en_get_attr = (function en_get_attr(attr){
return enfocus.core.extr_multi_node.call(null,(function (pnod){
return pnod.getAttribute(cljs.core.name.call(null,attr));
}));
});
/**
* returns the attribute on the selected element or elements.
* in cases where more than one element is selected you will
* receive a list of values
*/
enfocus.core.en_get_text = (function en_get_text(){
return enfocus.core.extr_multi_node.call(null,(function (pnod){
return goog.dom.getTextContent(pnod);
}));
});
/**
* returns the data on a selected node for a given key. If bubble is set will look at parent
*/
enfocus.core.en_get_data = (function() {
var en_get_data = null;
var en_get_data__1 = (function (ky){
return en_get_data.call(null,ky,false);
});
var en_get_data__2 = (function (ky,bubble){
return enfocus.core.extr_multi_node.call(null,(function (node){
return domina.get_data.call(null,ky,bubble);
}));
});
en_get_data = function(ky,bubble){
switch(arguments.length){
case 1:
return en_get_data__1.call(this,ky);
case 2:
return en_get_data__2.call(this,ky,bubble);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
en_get_data.cljs$lang$arity$1 = en_get_data__1;
en_get_data.cljs$lang$arity$2 = en_get_data__2;
return en_get_data;
})()
;
enfocus.core.reg_filt = cljs.core.atom.call(null,cljs.core.ObjMap.EMPTY);
/**
* filter allows you to apply function to futhur scope down what is returned by a selector
*/
enfocus.core.en_filter = (function en_filter(tst,trans){
return (function() {
var filt = null;
var filt__1 = (function (pnodes){
return filt.call(null,pnodes,null);
});
var filt__2 = (function (pnodes,chain){
var pnod_col = enfocus.core.nodes__GT_coll.call(null,pnodes);
var ttest = ((cljs.core.keyword_QMARK_.call(null,tst))?cljs.core.deref.call(null,enfocus.core.reg_filt).call(null,tst):tst);
var res = cljs.core.filter.call(null,ttest,pnod_col);
if((chain == null))
{return trans.call(null,res);
} else
{return trans.call(null,res,chain);
}
});
filt = function(pnodes,chain){
switch(arguments.length){
case 1:
return filt__1.call(this,pnodes);
case 2:
return filt__2.call(this,pnodes,chain);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
filt.cljs$lang$arity$1 = filt__1;
filt.cljs$lang$arity$2 = filt__2;
return filt;
})()
});
/**
* registers a filter for a given keyword
*/
enfocus.core.register_filter = (function register_filter(ky,func){
return cljs.core.swap_BANG_.call(null,enfocus.core.reg_filt,cljs.core.assoc,ky,func);
});
/**
* takes a list of options and returns the selected ones.
*/
enfocus.core.selected_options = (function selected_options(pnod){
return pnod.selected;
});
/**
* takes a list of radio or checkboxes and returns the checked ones
*/
enfocus.core.checked_radio_checkbox = (function checked_radio_checkbox(pnod){
return pnod.checked;
});
enfocus.core.register_filter.call(null,"\uFDD0'selected",enfocus.core.selected_options);
enfocus.core.register_filter.call(null,"\uFDD0'checked",enfocus.core.checked_radio_checkbox);
/**
* converts keywords, symbols and strings used in the enlive selector
* syntax to a string representing a standard css selector.  It also
* applys id masking if mask provided
*/
enfocus.core.create_sel_str = (function() {
var create_sel_str = null;
var create_sel_str__1 = (function (css_sel){
return create_sel_str.call(null,"",css_sel);
});
var create_sel_str__2 = (function (id_mask_sym,css_sel){
return cljs.core.apply.call(null,cljs.core.str,cljs.core.map.call(null,(function (p1__5089_SHARP_){
if(cljs.core.symbol_QMARK_.call(null,p1__5089_SHARP_))
{return enfocus.core.css_syms.call(null,p1__5089_SHARP_);
} else
{if(cljs.core.keyword_QMARK_.call(null,p1__5089_SHARP_))
{return [cljs.core.str(" "),cljs.core.str(cljs.core.name.call(null,p1__5089_SHARP_).replace("#",[cljs.core.str("#"),cljs.core.str(id_mask_sym)].join('')))].join('');
} else
{if(cljs.core.vector_QMARK_.call(null,p1__5089_SHARP_))
{return create_sel_str.call(null,p1__5089_SHARP_);
} else
{if(cljs.core.string_QMARK_.call(null,p1__5089_SHARP_))
{return p1__5089_SHARP_.replace("#",[cljs.core.str("#"),cljs.core.str(id_mask_sym)].join(''));
} else
{return null;
}
}
}
}
}),css_sel));
});
create_sel_str = function(id_mask_sym,css_sel){
switch(arguments.length){
case 1:
return create_sel_str__1.call(this,id_mask_sym);
case 2:
return create_sel_str__2.call(this,id_mask_sym,css_sel);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
create_sel_str.cljs$lang$arity$1 = create_sel_str__1;
create_sel_str.cljs$lang$arity$2 = create_sel_str__2;
return create_sel_str;
})()
;
/**
* takes either an enlive selector or a css3 selector and returns a set of nodes that match the selector
*/
enfocus.core.css_select = (function() {
var css_select = null;
var css_select__1 = (function (css_sel){
return css_select.call(null,"",document,css_sel);
});
var css_select__2 = (function (dom_node,css_sel){
return css_select.call(null,"",dom_node,css_sel);
});
var css_select__3 = (function (id_mask_sym,dom_node,css_sel){
enfocus.core.log_debug.call(null,dom_node);
enfocus.core.log_debug.call(null,cljs.core.pr_str.call(null,css_sel));
var sel = clojure.string.trim.call(null,enfocus.enlive.syntax.convert.call(null,enfocus.core.create_sel_str.call(null,id_mask_sym,css_sel)));
var ret = domina.css.sel.call(null,dom_node,sel);
return ret;
});
css_select = function(id_mask_sym,dom_node,css_sel){
switch(arguments.length){
case 1:
return css_select__1.call(this,id_mask_sym);
case 2:
return css_select__2.call(this,id_mask_sym,dom_node);
case 3:
return css_select__3.call(this,id_mask_sym,dom_node,css_sel);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
css_select.cljs$lang$arity$1 = css_select__1;
css_select.cljs$lang$arity$2 = css_select__2;
css_select.cljs$lang$arity$3 = css_select__3;
return css_select;
})()
;
enfocus.core.nil_t = (function nil_t(func){
var or__3824__auto__ = func;
if(cljs.core.truth_(or__3824__auto__))
{return or__3824__auto__;
} else
{return enfocus.core.en_remove_node;
}
});
/**
* @param {...*} var_args
*/
enfocus.core.i_at = (function() { 
var i_at__delegate = function (id_mask,node,trans){
if(cljs.core._EQ_.call(null,1,cljs.core.count.call(null,trans)))
{return cljs.core.first.call(null,trans).call(null,node);
} else
{var G__5092 = cljs.core.seq.call(null,cljs.core.partition.call(null,2,trans));
while(true){
if(G__5092)
{var vec__5093 = cljs.core.first.call(null,G__5092);
var sel = cljs.core.nth.call(null,vec__5093,0,null);
var t = cljs.core.nth.call(null,vec__5093,1,null);
enfocus.core.nil_t.call(null,t).call(null,enfocus.core.css_select.call(null,id_mask,node,sel));
{
var G__5094 = cljs.core.next.call(null,G__5092);
G__5092 = G__5094;
continue;
}
} else
{return null;
}
break;
}
}
};
var i_at = function (id_mask,node,var_args){
var trans = null;
if (goog.isDef(var_args)) {
  trans = cljs.core.array_seq(Array.prototype.slice.call(arguments, 2),0);
} 
return i_at__delegate.call(this, id_mask, node, trans);
};
i_at.cljs$lang$maxFixedArity = 2;
i_at.cljs$lang$applyTo = (function (arglist__5095){
var id_mask = cljs.core.first(arglist__5095);
var node = cljs.core.first(cljs.core.next(arglist__5095));
var trans = cljs.core.rest(cljs.core.next(arglist__5095));
return i_at__delegate(id_mask, node, trans);
});
i_at.cljs$lang$arity$variadic = i_at__delegate;
return i_at;
})()
;
/**
* @param {...*} var_args
*/
enfocus.core.at = (function() { 
var at__delegate = function (node,trans){
return cljs.core.apply.call(null,enfocus.core.i_at,"",node,trans);
};
var at = function (node,var_args){
var trans = null;
if (goog.isDef(var_args)) {
  trans = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);
} 
return at__delegate.call(this, node, trans);
};
at.cljs$lang$maxFixedArity = 1;
at.cljs$lang$applyTo = (function (arglist__5096){
var node = cljs.core.first(arglist__5096);
var trans = cljs.core.rest(arglist__5096);
return at__delegate(node, trans);
});
at.cljs$lang$arity$variadic = at__delegate;
return at;
})()
;
/**
* @param {...*} var_args
*/
enfocus.core.from = (function() { 
var from__delegate = function (node,trans){
if(cljs.core._EQ_.call(null,1,cljs.core.count.call(null,trans)))
{return cljs.core.first.call(null,trans).call(null,node);
} else
{return cljs.core.apply.call(null,cljs.core.hash_map,cljs.core.mapcat.call(null,(function (p__5099){
var vec__5100 = p__5099;
var ky = cljs.core.nth.call(null,vec__5100,0,null);
var sel = cljs.core.nth.call(null,vec__5100,1,null);
var ext = cljs.core.nth.call(null,vec__5100,2,null);
return cljs.core.PersistentVector.fromArray([ky,ext.call(null,enfocus.core.css_select.call(null,"",node,sel))], true);
}),cljs.core.partition.call(null,3,trans)));
}
};
var from = function (node,var_args){
var trans = null;
if (goog.isDef(var_args)) {
  trans = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);
} 
return from__delegate.call(this, node, trans);
};
from.cljs$lang$maxFixedArity = 1;
from.cljs$lang$applyTo = (function (arglist__5101){
var node = cljs.core.first(arglist__5101);
var trans = cljs.core.rest(arglist__5101);
return from__delegate(node, trans);
});
from.cljs$lang$arity$variadic = from__delegate;
return from;
})()
;
Text.prototype.domina$DomContent$ = true;
Text.prototype.domina$DomContent$nodes$arity$1 = (function (content){
return cljs.core.PersistentVector.fromArray([content], true);
});
Text.prototype.domina$DomContent$single_node$arity$1 = (function (content){
return content;
});
