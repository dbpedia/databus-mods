#/usr/bin/env bash

list_databusid_children() {
  id_path=$1

  if [[ $id_path == */* ]]; then
    parent_id_path=${id_path%/*}
  else
    parent_id_path=""
  fi
  if echo $id_path | grep -qEv "(/.*){4}"; then
    curl -s https://raw.databus.dbpedia.org/$parent_id_path | grep -Po '<td><a href="/\K([^"]+)' | sed 's|$|/|g'
  else
    curl -s https://raw.databus.dbpedia.org/$parent_id_path | grep -Po '<td><a href="/\K([^"]+)'
  fi
}

databusid()
{
  echo "DTABUSID($1)"
}

_databusid_completion()
{
  echo "#######################" >> log
  echo " _databusid_completion" >> log
  local cur prev #=${COMP_WORDS[COMP_CWORD]}
  _get_comp_words_by_ref -n "=" cur prev
#  echo "CUR=$cur" >> log
#  echo "PREV=$prev" >> log
#  echo "### list_databusid_children ###" >> log
#  echo "$(list_databusid_children "$cur")" >> log
#  echo "### COMPGEN ###" >> log
  echo "$(compgen -W "$(list_databusid_children "$cur")" -- "$cur")" >> log
  COMPREPLY=( $(compgen -W "$(list_databusid_children "$cur")" -- "$cur") )
  [[ $COMPREPLY == */ ]] && compopt -o nospace
}
complete -o filenames -F _databusid_completion databusid
#complete -W "now tomorrow never" dothis
