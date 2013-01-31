goog.provide('domina');
goog.require('cljs.core');
goog.require('domina.support');
goog.require('goog.dom.classes');
goog.require('goog.events');
goog.require('goog.dom.xml');
goog.require('goog.dom.forms');
goog.require('goog.dom');
goog.require('goog.string');
goog.require('clojure.string');
goog.require('goog.style');
goog.require('cljs.core');
domina.re_html = /<|&#?\w+;/;
domina.re_leading_whitespace = /^\s+/;
domina.re_xhtml_tag = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/i;
domina.re_tag_name = /<([\w:]+)/;
domina.re_no_inner_html = /<(?:script|style)/i;
domina.re_tbody = /<tbody/i;
var opt_wrapper_5110 = cljs.core.PersistentVector.fromArray([1,"<select multiple='multiple'>","</select>"], true);
var table_section_wrapper_5111 = cljs.core.PersistentVector.fromArray([1,"<table>","</table>"], true);
var cell_wrapper_5112 = cljs.core.PersistentVector.fromArray([3,"<table><tbody><tr>","</tr></tbody></table>"], true);
domina.wrap_map = cljs.core.ObjMap.fromObject(["col","\uFDD0'default","tfoot","caption","optgroup","legend","area","td","thead","th","option","tbody","tr","colgroup"],{"col":cljs.core.PersistentVector.fromArray([2,"<table><tbody></tbody><colgroup>","</colgroup></table>"], true),"\uFDD0'default":cljs.core.PersistentVector.fromArray([0,"",""], true),"tfoot":table_section_wrapper_5111,"caption":table_section_wrapper_5111,"optgroup":opt_wrapper_5110,"legend":cljs.core.PersistentVector.fromArray([1,"<fieldset>","</fieldset>"], true),"area":cljs.core.PersistentVector.fromArray([1,"<map>","</map>"], true),"td":cell_wrapper_5112,"thead":table_section_wrapper_5111,"th":cell_wrapper_5112,"option":opt_wrapper_5110,"tbody":table_section_wrapper_5111,"tr":cljs.core.PersistentVector.fromArray([2,"<table><tbody>","</tbody></table>"], true),"colgroup":table_section_wrapper_5111});
domina.remove_extraneous_tbody_BANG_ = (function remove_extraneous_tbody_BANG_(div,html){
var no_tbody_QMARK_ = cljs.core.not.call(null,cljs.core.re_find.call(null,domina.re_tbody,html));
var tbody = (((function (){var and__3822__auto__ = cljs.core._EQ_.call(null,domina.tag_name,"table");
if(and__3822__auto__)
{return no_tbody_QMARK_;
} else
{return and__3822__auto__;
}
})())?(function (){var and__3822__auto__ = div.firstChild;
if(cljs.core.truth_(and__3822__auto__))
{return div.firstChild.childNodes;
} else
{return and__3822__auto__;
}
})():(((function (){var and__3822__auto__ = cljs.core._EQ_.call(null,domina.start_wrap,"<table>");
if(and__3822__auto__)
{return no_tbody_QMARK_;
} else
{return and__3822__auto__;
}
})())?divchildNodes:cljs.core.PersistentVector.EMPTY));
var G__5114 = cljs.core.seq.call(null,tbody);
while(true){
if(G__5114)
{var child = cljs.core.first.call(null,G__5114);
if((function (){var and__3822__auto__ = cljs.core._EQ_.call(null,child.nodeName,"tbody");
if(and__3822__auto__)
{return cljs.core._EQ_.call(null,child.childNodes.length,0);
} else
{return and__3822__auto__;
}
})())
{child.parentNode.removeChild(child);
} else
{}
{
var G__5115 = cljs.core.next.call(null,G__5114);
G__5114 = G__5115;
continue;
}
} else
{return null;
}
break;
}
});
domina.restore_leading_whitespace_BANG_ = (function restore_leading_whitespace_BANG_(div,html){
return div.insertBefore(document.createTextNode(cljs.core.first.call(null,cljs.core.re_find.call(null,domina.re_leading_whitespace,html))),div.firstChild);
});
domina.html_to_dom = (function html_to_dom(html){
var html__$1 = clojure.string.replace.call(null,html,domina.re_xhtml_tag,"<$1></$2>");
var tag_name = [cljs.core.str(cljs.core.second.call(null,cljs.core.re_find.call(null,domina.re_tag_name,html__$1)))].join('').toLowerCase();
var vec__5117 = cljs.core._lookup.call(null,domina.wrap_map,tag_name,(new cljs.core.Keyword("\uFDD0'default")).call(null,domina.wrap_map));
var depth = cljs.core.nth.call(null,vec__5117,0,null);
var start_wrap = cljs.core.nth.call(null,vec__5117,1,null);
var end_wrap = cljs.core.nth.call(null,vec__5117,2,null);
var div = (function (){var wrapper = (function (){var div = document.createElement("div");
div.innerHTML = [cljs.core.str(start_wrap),cljs.core.str(html__$1),cljs.core.str(end_wrap)].join('');
return div;
})();
var level = depth;
while(true){
if((level > 0))
{{
var G__5118 = wrapper.lastChild;
var G__5119 = (level - 1);
wrapper = G__5118;
level = G__5119;
continue;
}
} else
{return wrapper;
}
break;
}
})();
if(cljs.core.truth_(domina.support.extraneous_tbody_QMARK_))
{domina.remove_extraneous_tbody_BANG_.call(null,div,html__$1);
} else
{}
if(cljs.core.truth_((function (){var and__3822__auto__ = cljs.core.not.call(null,domina.support.leading_whitespace_QMARK_);
if(and__3822__auto__)
{return cljs.core.re_find.call(null,domina.re_leading_whitespace,html__$1);
} else
{return and__3822__auto__;
}
})()))
{domina.restore_leading_whitespace_BANG_.call(null,div,html__$1);
} else
{}
return div.childNodes;
});
domina.string_to_dom = (function string_to_dom(s){
if(cljs.core.truth_(cljs.core.re_find.call(null,domina.re_html,s)))
{return domina.html_to_dom.call(null,s);
} else
{return document.createTextNode(s);
}
});
domina.DomContent = {};
domina.nodes = (function nodes(content){
if((function (){var and__3822__auto__ = content;
if(and__3822__auto__)
{return content.domina$DomContent$nodes$arity$1;
} else
{return and__3822__auto__;
}
})())
{return content.domina$DomContent$nodes$arity$1(content);
} else
{var x__812__auto__ = (((content == null))?null:content);
return (function (){var or__3824__auto__ = (domina.nodes[goog.typeOf(x__812__auto__)]);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{var or__3824__auto____$1 = (domina.nodes["_"]);
if(or__3824__auto____$1)
{return or__3824__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"DomContent.nodes",content);
}
}
})().call(null,content);
}
});
domina.single_node = (function single_node(nodeseq){
if((function (){var and__3822__auto__ = nodeseq;
if(and__3822__auto__)
{return nodeseq.domina$DomContent$single_node$arity$1;
} else
{return and__3822__auto__;
}
})())
{return nodeseq.domina$DomContent$single_node$arity$1(nodeseq);
} else
{var x__812__auto__ = (((nodeseq == null))?null:nodeseq);
return (function (){var or__3824__auto__ = (domina.single_node[goog.typeOf(x__812__auto__)]);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{var or__3824__auto____$1 = (domina.single_node["_"]);
if(or__3824__auto____$1)
{return or__3824__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"DomContent.single-node",nodeseq);
}
}
})().call(null,nodeseq);
}
});
domina._STAR_debug_STAR_ = true;
/**
* @param {...*} var_args
*/
domina.log_debug = (function() { 
var log_debug__delegate = function (mesg){
if(cljs.core.truth_((function (){var and__3822__auto__ = domina._STAR_debug_STAR_;
if(cljs.core.truth_(and__3822__auto__))
{return !(cljs.core._EQ_.call(null,window.console,undefined));
} else
{return and__3822__auto__;
}
})()))
{return console.log(cljs.core.apply.call(null,cljs.core.str,mesg));
} else
{return null;
}
};
var log_debug = function (var_args){
var mesg = null;
if (goog.isDef(var_args)) {
  mesg = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return log_debug__delegate.call(this, mesg);
};
log_debug.cljs$lang$maxFixedArity = 0;
log_debug.cljs$lang$applyTo = (function (arglist__5120){
var mesg = cljs.core.seq(arglist__5120);;
return log_debug__delegate(mesg);
});
log_debug.cljs$lang$arity$variadic = log_debug__delegate;
return log_debug;
})()
;
/**
* @param {...*} var_args
*/
domina.log = (function() { 
var log__delegate = function (mesg){
if(cljs.core.truth_(window.console))
{return console.log(cljs.core.apply.call(null,cljs.core.str,mesg));
} else
{return null;
}
};
var log = function (var_args){
var mesg = null;
if (goog.isDef(var_args)) {
  mesg = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);
} 
return log__delegate.call(this, mesg);
};
log.cljs$lang$maxFixedArity = 0;
log.cljs$lang$applyTo = (function (arglist__5121){
var mesg = cljs.core.seq(arglist__5121);;
return log__delegate(mesg);
});
log.cljs$lang$arity$variadic = log__delegate;
return log;
})()
;
/**
* Returns content containing a single node by looking up the given ID
*/
domina.by_id = (function by_id(id){
return goog.dom.getElement(cljs.core.name.call(null,id));
});
/**
* Returns content containing nodes which have the specified CSS class.
*/
domina.by_class = (function by_class(class_name){
if((void 0 === domina.t5125))
{goog.provide('domina.t5125');

/**
* @constructor
*/
domina.t5125 = (function (class_name,by_class,meta5126){
this.class_name = class_name;
this.by_class = by_class;
this.meta5126 = meta5126;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
domina.t5125.cljs$lang$type = true;
domina.t5125.cljs$lang$ctorPrSeq = (function (this__752__auto__){
return cljs.core.list.call(null,"domina/t5125");
});
domina.t5125.cljs$lang$ctorPrWriter = (function (this__752__auto__,writer__753__auto__,opt__754__auto__){
return cljs.core._write.call(null,writer__753__auto__,"domina/t5125");
});
domina.t5125.prototype.domina$DomContent$ = true;
domina.t5125.prototype.domina$DomContent$nodes$arity$1 = (function (_){
var self__ = this;
return domina.normalize_seq.call(null,goog.dom.getElementsByClass(cljs.core.name.call(null,self__.class_name)));
});
domina.t5125.prototype.domina$DomContent$single_node$arity$1 = (function (_){
var self__ = this;
return domina.normalize_seq.call(null,goog.dom.getElementByClass(cljs.core.name.call(null,self__.class_name)));
});
domina.t5125.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_5127){
var self__ = this;
return self__.meta5126;
});
domina.t5125.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_5127,meta5126__$1){
var self__ = this;
return (new domina.t5125(self__.class_name,self__.by_class,meta5126__$1));
});
} else
{}
return (new domina.t5125(class_name,by_class,null));
});
/**
* Gets all the child nodes of the elements in a content. Same as (xpath content '*') but more efficient.
*/
domina.children = (function children(content){
return cljs.core.doall.call(null,cljs.core.mapcat.call(null,goog.dom.getChildren,domina.nodes.call(null,content)));
});
/**
* Returns a deep clone of content.
*/
domina.clone = (function clone(content){
return cljs.core.map.call(null,(function (p1__5128_SHARP_){
return p1__5128_SHARP_.cloneNode(true);
}),domina.nodes.call(null,content));
});
/**
* Given a parent and child contents, appends each of the children to all of the parents. If there is more than one node in the parent content, clones the children for the additional parents. Returns the parent content.
*/
domina.append_BANG_ = (function append_BANG_(parent_content,child_content){
domina.apply_with_cloning.call(null,goog.dom.appendChild,parent_content,child_content);
return parent_content;
});
/**
* Given a parent and child contents, appends each of the children to all of the parents at the specified index. If there is more than one node in the parent content, clones the children for the additional parents. Returns the parent content.
*/
domina.insert_BANG_ = (function insert_BANG_(parent_content,child_content,idx){
domina.apply_with_cloning.call(null,(function (p1__5129_SHARP_,p2__5130_SHARP_){
return goog.dom.insertChildAt(p1__5129_SHARP_,p2__5130_SHARP_,idx);
}),parent_content,child_content);
return parent_content;
});
/**
* Given a parent and child contents, prepends each of the children to all of the parents. If there is more than one node in the parent content, clones the children for the additional parents. Returns the parent content.
*/
domina.prepend_BANG_ = (function prepend_BANG_(parent_content,child_content){
domina.insert_BANG_.call(null,parent_content,child_content,0);
return parent_content;
});
/**
* Given a content and some new content, inserts the new content immediately before the reference content. If there is more than one node in the reference content, clones the new content for each one.
*/
domina.insert_before_BANG_ = (function insert_before_BANG_(content,new_content){
domina.apply_with_cloning.call(null,(function (p1__5132_SHARP_,p2__5131_SHARP_){
return goog.dom.insertSiblingBefore(p2__5131_SHARP_,p1__5132_SHARP_);
}),content,new_content);
return content;
});
/**
* Given a content and some new content, inserts the new content immediately after the reference content. If there is more than one node in the reference content, clones the new content for each one.
*/
domina.insert_after_BANG_ = (function insert_after_BANG_(content,new_content){
domina.apply_with_cloning.call(null,(function (p1__5134_SHARP_,p2__5133_SHARP_){
return goog.dom.insertSiblingAfter(p2__5133_SHARP_,p1__5134_SHARP_);
}),content,new_content);
return content;
});
/**
* Given some old content and some new content, replaces the old content with new content. If there are multiple nodes in the old content, replaces each of them and clones the new content as necessary.
*/
domina.swap_content_BANG_ = (function swap_content_BANG_(old_content,new_content){
domina.apply_with_cloning.call(null,(function (p1__5136_SHARP_,p2__5135_SHARP_){
return goog.dom.replaceNode(p2__5135_SHARP_,p1__5136_SHARP_);
}),old_content,new_content);
return old_content;
});
/**
* Removes all the nodes in a content from the DOM and returns them.
*/
domina.detach_BANG_ = (function detach_BANG_(content){
return cljs.core.doall.call(null,cljs.core.map.call(null,goog.dom.removeNode,domina.nodes.call(null,content)));
});
/**
* Removes all the nodes in a content from the DOM. Returns nil.
*/
domina.destroy_BANG_ = (function destroy_BANG_(content){
return cljs.core.dorun.call(null,cljs.core.map.call(null,goog.dom.removeNode,domina.nodes.call(null,content)));
});
/**
* Removes all the child nodes in a content from the DOM. Returns the original content.
*/
domina.destroy_children_BANG_ = (function destroy_children_BANG_(content){
cljs.core.dorun.call(null,cljs.core.map.call(null,goog.dom.removeChildren,domina.nodes.call(null,content)));
return content;
});
/**
* Gets the value of a CSS property. Assumes content will be a single node. Name may be a string or keyword. Returns nil if there is no value set for the style.
*/
domina.style = (function style(content,name){
var s = goog.style.getStyle(domina.single_node.call(null,content),cljs.core.name.call(null,name));
if(cljs.core.truth_(clojure.string.blank_QMARK_.call(null,s)))
{return null;
} else
{return s;
}
});
/**
* Gets the value of an HTML attribute. Assumes content will be a single node. Name may be a stirng or keyword. Returns nil if there is no value set for the style.
*/
domina.attr = (function attr(content,name){
return domina.single_node.call(null,content).getAttribute(cljs.core.name.call(null,name));
});
/**
* Sets the value of a CSS property for each node in the content. Name may be a string or keyword. Value will be cast to a string, multiple values wil be concatenated.
* @param {...*} var_args
*/
domina.set_style_BANG_ = (function() { 
var set_style_BANG___delegate = function (content,name,value){
var G__5138_5139 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5138_5139)
{var n_5140 = cljs.core.first.call(null,G__5138_5139);
goog.style.setStyle(n_5140,cljs.core.name.call(null,name),cljs.core.apply.call(null,cljs.core.str,value));
{
var G__5141 = cljs.core.next.call(null,G__5138_5139);
G__5138_5139 = G__5141;
continue;
}
} else
{}
break;
}
return content;
};
var set_style_BANG_ = function (content,name,var_args){
var value = null;
if (goog.isDef(var_args)) {
  value = cljs.core.array_seq(Array.prototype.slice.call(arguments, 2),0);
} 
return set_style_BANG___delegate.call(this, content, name, value);
};
set_style_BANG_.cljs$lang$maxFixedArity = 2;
set_style_BANG_.cljs$lang$applyTo = (function (arglist__5142){
var content = cljs.core.first(arglist__5142);
var name = cljs.core.first(cljs.core.next(arglist__5142));
var value = cljs.core.rest(cljs.core.next(arglist__5142));
return set_style_BANG___delegate(content, name, value);
});
set_style_BANG_.cljs$lang$arity$variadic = set_style_BANG___delegate;
return set_style_BANG_;
})()
;
/**
* Sets the value of an HTML property for each node in the content. Name may be a string or keyword. Value will be cast to a string, multiple values wil be concatenated.
* @param {...*} var_args
*/
domina.set_attr_BANG_ = (function() { 
var set_attr_BANG___delegate = function (content,name,value){
var G__5144_5145 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5144_5145)
{var n_5146 = cljs.core.first.call(null,G__5144_5145);
n_5146.setAttribute(cljs.core.name.call(null,name),cljs.core.apply.call(null,cljs.core.str,value));
{
var G__5147 = cljs.core.next.call(null,G__5144_5145);
G__5144_5145 = G__5147;
continue;
}
} else
{}
break;
}
return content;
};
var set_attr_BANG_ = function (content,name,var_args){
var value = null;
if (goog.isDef(var_args)) {
  value = cljs.core.array_seq(Array.prototype.slice.call(arguments, 2),0);
} 
return set_attr_BANG___delegate.call(this, content, name, value);
};
set_attr_BANG_.cljs$lang$maxFixedArity = 2;
set_attr_BANG_.cljs$lang$applyTo = (function (arglist__5148){
var content = cljs.core.first(arglist__5148);
var name = cljs.core.first(cljs.core.next(arglist__5148));
var value = cljs.core.rest(cljs.core.next(arglist__5148));
return set_attr_BANG___delegate(content, name, value);
});
set_attr_BANG_.cljs$lang$arity$variadic = set_attr_BANG___delegate;
return set_attr_BANG_;
})()
;
/**
* Removes the specified HTML property for each node in the content. Name may be a string or keyword.
*/
domina.remove_attr_BANG_ = (function remove_attr_BANG_(content,name){
var G__5150_5151 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5150_5151)
{var n_5152 = cljs.core.first.call(null,G__5150_5151);
n_5152.removeAttribute(cljs.core.name.call(null,name));
{
var G__5153 = cljs.core.next.call(null,G__5150_5151);
G__5150_5151 = G__5153;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Parses a CSS style string and returns the properties as a map.
*/
domina.parse_style_attributes = (function parse_style_attributes(style){
return cljs.core.reduce.call(null,(function (acc,pair){
var vec__5155 = pair.split(/\s*:\s*/);
var k = cljs.core.nth.call(null,vec__5155,0,null);
var v = cljs.core.nth.call(null,vec__5155,1,null);
if(cljs.core.truth_((function (){var and__3822__auto__ = k;
if(cljs.core.truth_(and__3822__auto__))
{return v;
} else
{return and__3822__auto__;
}
})()))
{return cljs.core.assoc.call(null,acc,cljs.core.keyword.call(null,k.toLowerCase()),v);
} else
{return acc;
}
}),cljs.core.ObjMap.EMPTY,style.split(/\s*;\s*/));
});
/**
* Returns a map of the CSS styles/values. Assumes content will be a single node. Style names are returned as keywords.
*/
domina.styles = (function styles(content){
var style = domina.attr.call(null,content,"style");
if(cljs.core.string_QMARK_.call(null,style))
{return domina.parse_style_attributes.call(null,style);
} else
{if(cljs.core.truth_(style.cssText))
{return domina.parse_style_attributes.call(null,style.cssText);
} else
{return null;
}
}
});
/**
* Returns a map of the HTML attributes/values. Assumes content will be a single node. Attribute names are returned as keywords.
*/
domina.attrs = (function attrs(content){
var node = domina.single_node.call(null,content);
var attrs__$1 = node.attributes;
return cljs.core.reduce.call(null,cljs.core.conj,cljs.core.filter.call(null,cljs.core.complement.call(null,cljs.core.nil_QMARK_),cljs.core.map.call(null,(function (p1__5156_SHARP_){
var attr = attrs__$1.item(p1__5156_SHARP_);
var value = attr.nodeValue;
if((function (){var and__3822__auto__ = cljs.core.not_EQ_.call(null,null,value);
if(and__3822__auto__)
{return cljs.core.not_EQ_.call(null,"",value);
} else
{return and__3822__auto__;
}
})())
{return cljs.core.PersistentArrayMap.fromArrays([cljs.core.keyword.call(null,attr.nodeName.toLowerCase())],[attr.nodeValue]);
} else
{return null;
}
}),cljs.core.range.call(null,attrs__$1.length))));
});
/**
* Sets the specified CSS styles for each node in the content, given a map of names and values. Style names may be keywords or strings.
*/
domina.set_styles_BANG_ = (function set_styles_BANG_(content,styles){
var G__5159_5161 = cljs.core.seq.call(null,styles);
while(true){
if(G__5159_5161)
{var vec__5160_5162 = cljs.core.first.call(null,G__5159_5161);
var name_5163 = cljs.core.nth.call(null,vec__5160_5162,0,null);
var value_5164 = cljs.core.nth.call(null,vec__5160_5162,1,null);
domina.set_style_BANG_.call(null,content,name_5163,value_5164);
{
var G__5165 = cljs.core.next.call(null,G__5159_5161);
G__5159_5161 = G__5165;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Sets the specified CSS styles fpr each node in the content, given a map of names and values. Style names may be keywords or strings.
*/
domina.set_attrs_BANG_ = (function set_attrs_BANG_(content,attrs){
var G__5168_5170 = cljs.core.seq.call(null,attrs);
while(true){
if(G__5168_5170)
{var vec__5169_5171 = cljs.core.first.call(null,G__5168_5170);
var name_5172 = cljs.core.nth.call(null,vec__5169_5171,0,null);
var value_5173 = cljs.core.nth.call(null,vec__5169_5171,1,null);
domina.set_attr_BANG_.call(null,content,name_5172,value_5173);
{
var G__5174 = cljs.core.next.call(null,G__5168_5170);
G__5168_5170 = G__5174;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Returns true if the node has the specified CSS class. Assumes content is a single node.
*/
domina.has_class_QMARK_ = (function has_class_QMARK_(content,class$){
return goog.dom.classes.has(domina.single_node.call(null,content),class$);
});
/**
* Adds the specified CSS class to each node in the content.
*/
domina.add_class_BANG_ = (function add_class_BANG_(content,class$){
var G__5176_5177 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5176_5177)
{var node_5178 = cljs.core.first.call(null,G__5176_5177);
goog.dom.classes.add(node_5178,class$);
{
var G__5179 = cljs.core.next.call(null,G__5176_5177);
G__5176_5177 = G__5179;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Removes the specified CSS class from each node in the content.
*/
domina.remove_class_BANG_ = (function remove_class_BANG_(content,class$){
var G__5181_5182 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5181_5182)
{var node_5183 = cljs.core.first.call(null,G__5181_5182);
goog.dom.classes.remove(node_5183,class$);
{
var G__5184 = cljs.core.next.call(null,G__5181_5182);
G__5181_5182 = G__5184;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Returns a seq of all the CSS classes currently applied to a node. Assumes content is a single node.
*/
domina.classes = (function classes(content){
return cljs.core.seq.call(null,goog.dom.classes.get(domina.single_node.call(null,content)));
});
/**
* Sets the class attribute of the content nodes to classes, which can
* be either a class attribute string or a seq of classname strings.
*/
domina.set_classes_BANG_ = (function set_classes_BANG_(content,classes){
var classes_5187__$1 = ((cljs.core.coll_QMARK_.call(null,classes))?clojure.string.join.call(null," ",classes):classes);
var G__5186_5188 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5186_5188)
{var node_5189 = cljs.core.first.call(null,G__5186_5188);
goog.dom.classes.set(node_5189,classes_5187__$1);
{
var G__5190 = cljs.core.next.call(null,G__5186_5188);
G__5186_5188 = G__5190;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Returns the text of a node. Assumes content is a single node. For consistency across browsers, will always trim whitespace of the beginning and end of the returned text.
*/
domina.text = (function text(content){
return goog.string.trim(goog.dom.getTextContent(domina.single_node.call(null,content)));
});
/**
* Sets the text value of all the nodes in the given content.
*/
domina.set_text_BANG_ = (function set_text_BANG_(content,value){
var G__5192_5193 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5192_5193)
{var node_5194 = cljs.core.first.call(null,G__5192_5193);
goog.dom.setTextContent(node_5194,value);
{
var G__5195 = cljs.core.next.call(null,G__5192_5193);
G__5192_5193 = G__5195;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Returns the value of a node (presumably a form field). Assumes content is a single node.
*/
domina.value = (function value(content){
return goog.dom.forms.getValue(domina.single_node.call(null,content));
});
/**
* Sets the value of all the nodes (presumably form fields) in the given content.
*/
domina.set_value_BANG_ = (function set_value_BANG_(content,value){
var G__5197_5198 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5197_5198)
{var node_5199 = cljs.core.first.call(null,G__5197_5198);
goog.dom.forms.setValue(node_5199,value);
{
var G__5200 = cljs.core.next.call(null,G__5197_5198);
G__5197_5198 = G__5200;
continue;
}
} else
{}
break;
}
return content;
});
/**
* Returns the innerHTML of a node. Assumes content is a single node.
*/
domina.html = (function html(content){
return domina.single_node.call(null,content).innerHTML;
});
domina.replace_children_BANG_ = (function replace_children_BANG_(content,inner_content){
return domina.append_BANG_.call(null,domina.destroy_children_BANG_.call(null,content),inner_content);
});
domina.set_inner_html_BANG_ = (function set_inner_html_BANG_(content,html_string){
var allows_inner_html_QMARK_ = cljs.core.not.call(null,cljs.core.re_find.call(null,domina.re_no_inner_html,html_string));
var leading_whitespace_QMARK_ = cljs.core.re_find.call(null,domina.re_leading_whitespace,html_string);
var tag_name = [cljs.core.str(cljs.core.second.call(null,cljs.core.re_find.call(null,domina.re_tag_name,html_string)))].join('').toLowerCase();
var special_tag_QMARK_ = cljs.core.contains_QMARK_.call(null,domina.wrap_map,tag_name);
if(cljs.core.truth_((function (){var and__3822__auto__ = allows_inner_html_QMARK_;
if(and__3822__auto__)
{var and__3822__auto____$1 = (function (){var or__3824__auto__ = domina.support.leading_whitespace_QMARK_;
if(cljs.core.truth_(or__3824__auto__))
{return or__3824__auto__;
} else
{return cljs.core.not.call(null,leading_whitespace_QMARK_);
}
})();
if(cljs.core.truth_(and__3822__auto____$1))
{return !(special_tag_QMARK_);
} else
{return and__3822__auto____$1;
}
} else
{return and__3822__auto__;
}
})()))
{var value_5205 = clojure.string.replace.call(null,html_string,domina.re_xhtml_tag,"<$1></$2>");
try{var G__5204_5206 = cljs.core.seq.call(null,domina.nodes.call(null,content));
while(true){
if(G__5204_5206)
{var node_5207 = cljs.core.first.call(null,G__5204_5206);
goog.events.removeAll(node_5207);
node_5207.innerHTML = value_5205;
{
var G__5208 = cljs.core.next.call(null,G__5204_5206);
G__5204_5206 = G__5208;
continue;
}
} else
{}
break;
}
}catch (e5203){if(cljs.core.instance_QMARK_.call(null,domina.Exception,e5203))
{var e_5209 = e5203;
domina.replace_children_BANG_.call(null,content,value_5205);
} else
{if("\uFDD0'else")
{throw e5203;
} else
{}
}
}} else
{domina.replace_children_BANG_.call(null,content,html_string);
}
return content;
});
/**
* Sets the innerHTML value for all the nodes in the given content.
*/
domina.set_html_BANG_ = (function set_html_BANG_(content,inner_content){
if(cljs.core.string_QMARK_.call(null,inner_content))
{return domina.set_inner_html_BANG_.call(null,content,inner_content);
} else
{return domina.replace_children_BANG_.call(null,content,inner_content);
}
});
/**
* Returns data associated with a node for a given key. Assumes
* content is a single node. If the bubble parameter is set to true,
* will search parent nodes if the key is not found.
*/
domina.get_data = (function() {
var get_data = null;
var get_data__2 = (function (node,key){
return get_data.call(null,node,key,false);
});
var get_data__3 = (function (node,key,bubble){
var m = domina.single_node.call(null,node).__domina_data;
var value = (cljs.core.truth_(m)?cljs.core._lookup.call(null,m,key,null):null);
if(cljs.core.truth_((function (){var and__3822__auto__ = bubble;
if(cljs.core.truth_(and__3822__auto__))
{return (value == null);
} else
{return and__3822__auto__;
}
})()))
{var temp__3974__auto__ = domina.single_node.call(null,node).parentNode;
if(cljs.core.truth_(temp__3974__auto__))
{var parent = temp__3974__auto__;
return get_data.call(null,parent,key,true);
} else
{return null;
}
} else
{return value;
}
});
get_data = function(node,key,bubble){
switch(arguments.length){
case 2:
return get_data__2.call(this,node,key);
case 3:
return get_data__3.call(this,node,key,bubble);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
get_data.cljs$lang$arity$2 = get_data__2;
get_data.cljs$lang$arity$3 = get_data__3;
return get_data;
})()
;
/**
* Sets a data on the node for a given key. Assumes content is a
* single node. Data should be ClojureScript values and data structures
* only; using other objects as data may result in memory leaks on some
* browsers.
*/
domina.set_data_BANG_ = (function set_data_BANG_(node,key,value){
var m = (function (){var or__3824__auto__ = domina.single_node.call(null,node).__domina_data;
if(cljs.core.truth_(or__3824__auto__))
{return or__3824__auto__;
} else
{return cljs.core.ObjMap.EMPTY;
}
})();
return domina.single_node.call(null,node).__domina_data = cljs.core.assoc.call(null,m,key,value);
});
/**
* Takes a two-arg function, a reference DomContent and new
* DomContent. Applies the function for each reference / content
* combination. Uses clones of the new content for each additional
* parent after the first.
*/
domina.apply_with_cloning = (function apply_with_cloning(f,parent_content,child_content){
var parents = domina.nodes.call(null,parent_content);
var children = domina.nodes.call(null,child_content);
var first_child = (function (){var frag = document.createDocumentFragment();
var G__5213_5214 = cljs.core.seq.call(null,children);
while(true){
if(G__5213_5214)
{var child_5215 = cljs.core.first.call(null,G__5213_5214);
frag.appendChild(child_5215);
{
var G__5216 = cljs.core.next.call(null,G__5213_5214);
G__5213_5214 = G__5216;
continue;
}
} else
{}
break;
}
return frag;
})();
var other_children = cljs.core.doall.call(null,cljs.core.repeatedly.call(null,(cljs.core.count.call(null,parents) - 1),(function (){
return first_child.cloneNode(true);
})));
if(cljs.core.seq.call(null,parents))
{f.call(null,cljs.core.first.call(null,parents),first_child);
return cljs.core.doall.call(null,cljs.core.map.call(null,(function (p1__5210_SHARP_,p2__5211_SHARP_){
return f.call(null,p1__5210_SHARP_,p2__5211_SHARP_);
}),cljs.core.rest.call(null,parents),other_children));
} else
{return null;
}
});
domina.lazy_nl_via_item = (function() {
var lazy_nl_via_item = null;
var lazy_nl_via_item__1 = (function (nl){
return lazy_nl_via_item.call(null,nl,0);
});
var lazy_nl_via_item__2 = (function (nl,n){
if((n < nl.length))
{return (new cljs.core.LazySeq(null,false,(function (){
return cljs.core.cons.call(null,nl.item(n),lazy_nl_via_item.call(null,nl,(n + 1)));
}),null));
} else
{return null;
}
});
lazy_nl_via_item = function(nl,n){
switch(arguments.length){
case 1:
return lazy_nl_via_item__1.call(this,nl);
case 2:
return lazy_nl_via_item__2.call(this,nl,n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
lazy_nl_via_item.cljs$lang$arity$1 = lazy_nl_via_item__1;
lazy_nl_via_item.cljs$lang$arity$2 = lazy_nl_via_item__2;
return lazy_nl_via_item;
})()
;
domina.lazy_nl_via_array_ref = (function() {
var lazy_nl_via_array_ref = null;
var lazy_nl_via_array_ref__1 = (function (nl){
return lazy_nl_via_array_ref.call(null,nl,0);
});
var lazy_nl_via_array_ref__2 = (function (nl,n){
if((n < nl.length))
{return (new cljs.core.LazySeq(null,false,(function (){
return cljs.core.cons.call(null,(nl[n]),lazy_nl_via_array_ref.call(null,nl,(n + 1)));
}),null));
} else
{return null;
}
});
lazy_nl_via_array_ref = function(nl,n){
switch(arguments.length){
case 1:
return lazy_nl_via_array_ref__1.call(this,nl);
case 2:
return lazy_nl_via_array_ref__2.call(this,nl,n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
lazy_nl_via_array_ref.cljs$lang$arity$1 = lazy_nl_via_array_ref__1;
lazy_nl_via_array_ref.cljs$lang$arity$2 = lazy_nl_via_array_ref__2;
return lazy_nl_via_array_ref;
})()
;
/**
* A lazy seq view of a js/NodeList, or other array-like javascript things
*/
domina.lazy_nodelist = (function lazy_nodelist(nl){
if(cljs.core.truth_(nl.item))
{return domina.lazy_nl_via_item.call(null,nl);
} else
{return domina.lazy_nl_via_array_ref.call(null,nl);
}
});
domina.array_like_QMARK_ = (function array_like_QMARK_(obj){
var and__3822__auto__ = obj;
if(cljs.core.truth_(and__3822__auto__))
{return obj.length;
} else
{return and__3822__auto__;
}
});
/**
* Some versions of IE have things that are like arrays in that they
* respond to .length, but are not arrays nor NodeSets. This returns a
* real sequence view of such objects. If passed an object that is not
* a logical sequence at all, returns a single-item seq containing the
* object.
*/
domina.normalize_seq = (function normalize_seq(list_thing){
if((list_thing == null))
{return cljs.core.List.EMPTY;
} else
{if((function (){var G__5218 = list_thing;
if(G__5218)
{if((function (){var or__3824__auto__ = (G__5218.cljs$lang$protocol_mask$partition0$ & 8388608);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{return G__5218.cljs$core$ISeqable$;
}
})())
{return true;
} else
{if((!G__5218.cljs$lang$protocol_mask$partition0$))
{return cljs.core.type_satisfies_.call(null,cljs.core.ISeqable,G__5218);
} else
{return false;
}
}
} else
{return cljs.core.type_satisfies_.call(null,cljs.core.ISeqable,G__5218);
}
})())
{return cljs.core.seq.call(null,list_thing);
} else
{if(cljs.core.truth_(domina.array_like_QMARK_.call(null,list_thing)))
{return domina.lazy_nodelist.call(null,list_thing);
} else
{if("\uFDD0'default")
{return cljs.core.seq.call(null,cljs.core.PersistentVector.fromArray([list_thing], true));
} else
{return null;
}
}
}
}
});
(domina.DomContent["_"] = true);
(domina.nodes["_"] = (function (content){
if((content == null))
{return cljs.core.List.EMPTY;
} else
{if((function (){var G__5219 = content;
if(G__5219)
{if((function (){var or__3824__auto__ = (G__5219.cljs$lang$protocol_mask$partition0$ & 8388608);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{return G__5219.cljs$core$ISeqable$;
}
})())
{return true;
} else
{if((!G__5219.cljs$lang$protocol_mask$partition0$))
{return cljs.core.type_satisfies_.call(null,cljs.core.ISeqable,G__5219);
} else
{return false;
}
}
} else
{return cljs.core.type_satisfies_.call(null,cljs.core.ISeqable,G__5219);
}
})())
{return cljs.core.seq.call(null,content);
} else
{if(cljs.core.truth_(domina.array_like_QMARK_.call(null,content)))
{return domina.lazy_nodelist.call(null,content);
} else
{if("\uFDD0'default")
{return cljs.core.seq.call(null,cljs.core.PersistentVector.fromArray([content], true));
} else
{return null;
}
}
}
}
}));
(domina.single_node["_"] = (function (content){
if((content == null))
{return null;
} else
{if((function (){var G__5220 = content;
if(G__5220)
{if((function (){var or__3824__auto__ = (G__5220.cljs$lang$protocol_mask$partition0$ & 8388608);
if(or__3824__auto__)
{return or__3824__auto__;
} else
{return G__5220.cljs$core$ISeqable$;
}
})())
{return true;
} else
{if((!G__5220.cljs$lang$protocol_mask$partition0$))
{return cljs.core.type_satisfies_.call(null,cljs.core.ISeqable,G__5220);
} else
{return false;
}
}
} else
{return cljs.core.type_satisfies_.call(null,cljs.core.ISeqable,G__5220);
}
})())
{return cljs.core.first.call(null,content);
} else
{if(cljs.core.truth_(domina.array_like_QMARK_.call(null,content)))
{return content.item(0);
} else
{if("\uFDD0'default")
{return content;
} else
{return null;
}
}
}
}
}));
(domina.DomContent["string"] = true);
(domina.nodes["string"] = (function (s){
return cljs.core.doall.call(null,domina.nodes.call(null,domina.string_to_dom.call(null,s)));
}));
(domina.single_node["string"] = (function (s){
return domina.single_node.call(null,domina.string_to_dom.call(null,s));
}));
if(cljs.core.truth_((typeof NodeList != 'undefined')))
{NodeList.prototype.cljs$core$ISeqable$ = true;
NodeList.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (nodelist){
return domina.lazy_nodelist.call(null,nodelist);
});
NodeList.prototype.cljs$core$IIndexed$ = true;
NodeList.prototype.cljs$core$IIndexed$_nth$arity$2 = (function (nodelist,n){
return nodelist.item(n);
});
NodeList.prototype.cljs$core$IIndexed$_nth$arity$3 = (function (nodelist,n,not_found){
if((nodelist.length <= n))
{return not_found;
} else
{return cljs.core.nth.call(null,nodelist,n);
}
});
NodeList.prototype.cljs$core$ICounted$ = true;
NodeList.prototype.cljs$core$ICounted$_count$arity$1 = (function (nodelist){
return nodelist.length;
});
} else
{}
if(cljs.core.truth_((typeof StaticNodeList != 'undefined')))
{StaticNodeList.prototype.cljs$core$ISeqable$ = true;
StaticNodeList.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (nodelist){
return domina.lazy_nodelist.call(null,nodelist);
});
StaticNodeList.prototype.cljs$core$IIndexed$ = true;
StaticNodeList.prototype.cljs$core$IIndexed$_nth$arity$2 = (function (nodelist,n){
return nodelist.item(n);
});
StaticNodeList.prototype.cljs$core$IIndexed$_nth$arity$3 = (function (nodelist,n,not_found){
if((nodelist.length <= n))
{return not_found;
} else
{return cljs.core.nth.call(null,nodelist,n);
}
});
StaticNodeList.prototype.cljs$core$ICounted$ = true;
StaticNodeList.prototype.cljs$core$ICounted$_count$arity$1 = (function (nodelist){
return nodelist.length;
});
} else
{}
if(cljs.core.truth_((typeof HTMLCollection != 'undefined')))
{HTMLCollection.prototype.cljs$core$ISeqable$ = true;
HTMLCollection.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (coll){
return domina.lazy_nodelist.call(null,coll);
});
HTMLCollection.prototype.cljs$core$IIndexed$ = true;
HTMLCollection.prototype.cljs$core$IIndexed$_nth$arity$2 = (function (coll,n){
return coll.item(n);
});
HTMLCollection.prototype.cljs$core$IIndexed$_nth$arity$3 = (function (coll,n,not_found){
if((coll.length <= n))
{return not_found;
} else
{return cljs.core.nth.call(null,coll,n);
}
});
HTMLCollection.prototype.cljs$core$ICounted$ = true;
HTMLCollection.prototype.cljs$core$ICounted$_count$arity$1 = (function (coll){
return coll.length;
});
} else
{}
