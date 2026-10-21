BEGIN;

-- nbparticipants
UPDATE evenement SET nbparticipants = 0 WHERE nbparticipants IS NULL;
ALTER TABLE evenement ALTER COLUMN nbparticipants SET DEFAULT 0;
ALTER TABLE evenement ALTER COLUMN nbparticipants SET NOT NULL;

-- compteurs de candidats
ALTER TABLE evenement ADD COLUMN IF NOT EXISTS nbcandidatshommes integer NOT NULL DEFAULT 0;
ALTER TABLE evenement ADD COLUMN IF NOT EXISTS nbcandidatsfemmes integer NOT NULL DEFAULT 0;
ALTER TABLE evenement ADD COLUMN IF NOT EXISTS nbcandidatsvalideshommes integer NOT NULL DEFAULT 0;
ALTER TABLE evenement ADD COLUMN IF NOT EXISTS nbcandidatsvalidesfemmes integer NOT NULL DEFAULT 0;
ALTER TABLE evenement DROP COLUMN IF EXISTS nbcandidats;
ALTER TABLE evenement DROP COLUMN IF EXISTS nbcandidatsvalides;

-- statut : ordinal -> STRING
ALTER TABLE evenement DROP CONSTRAINT IF EXISTS evenement_statut_check;
ALTER TABLE evenement ALTER COLUMN statut DROP DEFAULT;
ALTER TABLE evenement ALTER COLUMN statut TYPE varchar(32)
  USING CASE COALESCE(statut, 0)
    WHEN 0 THEN 'DEMANDE'
    WHEN 1 THEN 'CONFLIT'
    WHEN 2 THEN 'VALIDE'
    WHEN 3 THEN 'SUPPRIME'
    WHEN 4 THEN 'REFUSE'
  END;
ALTER TABLE evenement ALTER COLUMN statut SET DEFAULT 'DEMANDE';
ALTER TABLE evenement ALTER COLUMN statut SET NOT NULL;

-- colonnes résiduelles
ALTER TABLE evenement
  DROP COLUMN IF EXISTS uuiddeleguectr,
  DROP COLUMN IF EXISTS uuiddemandeur,
  DROP COLUMN IF EXISTS uuidorganisateur,
  DROP COLUMN IF EXISTS uuidpartenaire,
  DROP COLUMN IF EXISTS uuidpresidentjury,
  DROP COLUMN IF EXISTS uuidrepcibpl,
  DROP COLUMN IF EXISTS uuidtype;

COMMIT;